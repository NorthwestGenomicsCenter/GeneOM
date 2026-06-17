package org.uw.nwgc.multiomics.util.coordinates_reader.detector;

import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate.ColumnType;
import org.uw.nwgc.multiomics.util.coordinates_reader.parser.ChromosomeNormalizer;
import org.uw.nwgc.multiomics.util.coordinates_reader.parser.GeneNameValidator;
import org.uw.nwgc.multiomics.util.coordinates_reader.parser.IntervalParser;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Scores individual columns for likelihood of containing genomic information.
 *
 * <p>Scoring is additive: header-name signals, value-pattern signals, and
 * cross-column coherence signals each contribute to the final 0–1 score.
 * Results above a configurable threshold are returned as {@link ColumnCandidate}s.</p>
 */
public class ColumnScorer {

    /** Configuration for scoring thresholds. */
    public static class Config {
        /** Minimum score to be included in the candidate list. */
        public double minScore = 0.15;

        /** Fraction of sampled values that must match to count toward the score. */
        public double valueMatchThreshold = 0.40;

        /** Number of non-header rows to sample for value analysis. */
        public int sampleSize = 30;

        /** Minimum fraction of sampled values that must be non-empty. */
        public double minNonEmptyFraction = 0.20;
    }

    // ── Header keyword patterns ────────────────────────────────────────────

    private static final Pattern CHR_HEADER = Pattern.compile(
            "(?i)^(?:chr(?:om(?:osome)?)?|chrom(?:osome)?|seqname[s]?|contig|scaffold|sequence)$");

    private static final Pattern START_HEADER = Pattern.compile(
            "(?i)^(?:start|chromstart|txstart|cdsstart|tstart|pos(?:ition)?(?:_?start)?|begin|from|left)$");

    private static final Pattern END_HEADER = Pattern.compile(
            "(?i)^(?:end|chromend|txend|cdsend|tend|pos(?:ition)?(?:_?end)?|stop|to|right)$");

    private static final Pattern POSITION_HEADER = Pattern.compile(
            "(?i)^(?:pos(?:ition)?|bp|basepair|loc(?:ation)?|coordinate[s]?|snp_?pos|variant_?pos)$");

    private static final Pattern INTERVAL_HEADER = Pattern.compile(
            "(?i)(?:interval|region|locus|loci|coordinates?|coords?|range|location|segment|band|cytoband)" +
            "|(?:grch3[678])|(?:hg(?:19|38|18))|(?:mm(?:10|39))");

    private static final Pattern STRAND_HEADER = Pattern.compile(
            "(?i)^strand$");

    private static final Pattern POSITION_VALUE = Pattern.compile("^[\\d,_]{4,12}$");

    // Rough human genome bounds for sanity checking
    private static final long MAX_CHROMOSOME_LENGTH = 250_000_000L;
    private static final long MIN_PLAUSIBLE_POSITION = 1L;

    private final Config config;

    public ColumnScorer() { this(new Config()); }
    public ColumnScorer(Config config) { this.config = config; }

    /**
     * Score a single column and return a {@link ColumnCandidate} if above threshold.
     *
     * @param columnIndex 0-based column index
     * @param header      column header string, may be null
     * @param values      all data values in this column (excluding header)
     * @return populated candidate, or empty if score is below threshold
     */
    public Optional<ColumnCandidate> score(int columnIndex, String header, List<String> values) {

        // Sample non-empty values
        List<String> nonEmpty = values.stream()
                .filter(v -> v != null && !v.isBlank())
                .limit(config.sampleSize)
                .collect(Collectors.toList());

        if (nonEmpty.isEmpty()) return Optional.empty();

        // Fractions of total non-null values that are non-empty
        long totalNonNull = values.stream().filter(Objects::nonNull).count();
        double nonEmptyFraction = totalNonNull == 0 ? 0 : (double) nonEmpty.size() / totalNonNull;
        if (nonEmptyFraction < config.minNonEmptyFraction && nonEmpty.size() < 3) return Optional.empty();

        ScoreAccumulator acc = new ScoreAccumulator(header, nonEmpty);

        scoreHeaderName(acc);
        scoreIntervalPattern(acc);
        scoreChromosomePattern(acc);
        scorePositionPattern(acc);
        scoreGenePattern(acc);
        scoreStrandPattern(acc);

        double finalScore = Math.min(1.0, acc.totalScore());
        if (finalScore < config.minScore) return Optional.empty();

        ColumnType type = acc.dominantType();
        String genome = ChromosomeNormalizer.detectAssemblyHint(header).orElse(null);
        if (genome == null) genome = acc.detectedGenome;

        return Optional.of(ColumnCandidate.builder()
                .columnIndex(columnIndex)
                .headerName(header)
                .columnType(type)
                .score(finalScore)
                .sampleValues(nonEmpty.subList(0, Math.min(5, nonEmpty.size())))
                .detectionReason(acc.reasons())
                .genome(genome)
                .build());
    }

    // ── Private scoring methods ────────────────────────────────────────────

    private void scoreHeaderName(ScoreAccumulator acc) {
        String h = acc.header;
        if (h == null || h.isBlank()) return;

        if (CHR_HEADER.matcher(h).matches()) {
            acc.add(ColumnType.CHROMOSOME, 0.50, "header matches chromosome keywords");
        } else if (START_HEADER.matcher(h).matches()) {
            acc.add(ColumnType.START_POSITION, 0.50, "header matches start-position keywords");
        } else if (END_HEADER.matcher(h).matches()) {
            acc.add(ColumnType.END_POSITION, 0.50, "header matches end-position keywords");
        } else if (POSITION_HEADER.matcher(h).matches()) {
            acc.add(ColumnType.POSITION, 0.45, "header matches position keywords");
        } else if (INTERVAL_HEADER.matcher(h).find()) {
            acc.add(ColumnType.INTERVAL, 0.45, "header contains interval/region keywords");
        } else if (STRAND_HEADER.matcher(h).matches()) {
            acc.add(ColumnType.STRAND, 0.70, "header is 'strand'");
        } else if (GeneNameValidator.isGeneHeader(h)) {
            acc.add(ColumnType.GENE_NAME, 0.55, "header matches gene-name keywords");
        }

        // Assembly hint boosts
        if (ChromosomeNormalizer.detectAssemblyHint(h).isPresent()) {
            acc.boost(0.10, "header contains assembly version hint");
        }
    }

    private void scoreIntervalPattern(ScoreAccumulator acc) {
        long matches = acc.nonEmpty.stream()
                .filter(v -> IntervalParser.parse(v).filter(c -> c.isInterval()).isPresent())
                .count();
        double frac = (double) matches / acc.nonEmpty.size();

        if (frac >= 0.70) {
            acc.add(ColumnType.INTERVAL, 0.55, String.format("%.0f%% of values parse as intervals", frac * 100));
            // Try to detect assembly from values themselves
            acc.nonEmpty.stream()
                    .flatMap(v -> IntervalParser.parse(v).stream())
                    .filter(c -> c.getChromosome() != null)
                    .findFirst(); // side-effect-free — assembly detection would need value text
        } else if (frac >= 0.40) {
            acc.add(ColumnType.INTERVAL, 0.30, String.format("%.0f%% of values resemble intervals", frac * 100));
        } else if (frac >= 0.15) {
            acc.add(ColumnType.INTERVAL, 0.12, String.format("%.0f%% of values weakly resemble intervals", frac * 100));
        }
    }

    private void scoreChromosomePattern(ScoreAccumulator acc) {
        long matches = acc.nonEmpty.stream()
                .filter(v -> ChromosomeNormalizer.looksLikeChromosome(v))
                .count();
        double frac = (double) matches / acc.nonEmpty.size();

        // Compute variety: a real chromosome column should have a small set of unique values
        long unique = acc.nonEmpty.stream().map(String::trim).distinct().count();
        boolean lowCardinality = unique <= 25;  // humans have at most 25 standard sequences

        if (frac >= 0.70 && lowCardinality) {
            acc.add(ColumnType.CHROMOSOME, 0.50, String.format("%.0f%% of values are chromosome names (%d unique)", frac * 100, unique));
        } else if (frac >= 0.70) {
            acc.add(ColumnType.CHROMOSOME, 0.30, String.format("%.0f%% of values look like chromosomes but high cardinality", frac * 100));
        } else if (frac >= 0.40) {
            acc.add(ColumnType.CHROMOSOME, 0.20, "some values resemble chromosome names");
        }
    }

    private void scorePositionPattern(ScoreAccumulator acc) {
        List<Long> positions = acc.nonEmpty.stream()
                .filter(v -> POSITION_VALUE.matcher(v.trim()).matches())
                .map(v -> {
                    try { return Long.parseLong(v.replace(",", "").replace("_", "")); }
                    catch (NumberFormatException e) { return -1L; }
                })
                .filter(n -> n >= MIN_PLAUSIBLE_POSITION && n <= MAX_CHROMOSOME_LENGTH)
                .collect(Collectors.toList());

        double frac = (double) positions.size() / acc.nonEmpty.size();
        if (frac < 0.50) return;

        // Genomic positions cluster in a biologically plausible range
        // and have variance consistent with genome-scale distributions
        if (!positions.isEmpty()) {
            long min = Collections.min(positions);
            long max = Collections.max(positions);
            long range = max - min;

            boolean plausibleRange = range > 100 && max <= MAX_CHROMOSOME_LENGTH;

            // Heuristic: if all values are identical or tiny range, probably not positions
            if (range == 0) return;

            double baseScore = plausibleRange ? 0.35 : 0.15;
            ColumnType type = (acc.dominantType() == ColumnType.START_POSITION
                    || acc.dominantType() == ColumnType.END_POSITION)
                    ? acc.dominantType() : ColumnType.POSITION;

            acc.add(type, baseScore,
                    String.format("%.0f%% of values are plausible genomic positions (range %,d–%,d)", frac * 100, min, max));
        }
    }

    private void scoreGenePattern(ScoreAccumulator acc) {
        long geneSymbols = acc.nonEmpty.stream()
                .filter(GeneNameValidator::isLikelyGeneSymbol)
                .count();
        double frac = (double) geneSymbols / acc.nonEmpty.size();

        long ensemblIds = acc.nonEmpty.stream()
                .filter(v -> v.trim().matches("(?i)^ENS[A-Z]{0,4}[GT]\\d{11}.*$"))
                .count();

        long rsIds = acc.nonEmpty.stream()
                .filter(v -> v.trim().matches("(?i)^rs\\d+$"))
                .count();

        if (ensemblIds > 0 && (double) ensemblIds / acc.nonEmpty.size() >= 0.40) {
            boolean isTranscript = acc.nonEmpty.stream()
                    .anyMatch(v -> v.trim().matches("(?i)^ENS[A-Z]{0,4}T\\d{11}.*$"));
            ColumnType t = isTranscript ? ColumnType.TRANSCRIPT_ID : ColumnType.GENE_ID;
            acc.add(t, 0.60, String.format("%.0f%% Ensembl IDs detected", (double) ensemblIds / acc.nonEmpty.size() * 100));
        }

        if ((double) rsIds / acc.nonEmpty.size() >= 0.40) {
            acc.add(ColumnType.VARIANT_ID, 0.65, "majority of values are rsIDs");
        }

        if (frac >= 0.65) {
            acc.add(ColumnType.GENE_NAME, 0.40, String.format("%.0f%% of values resemble HGNC gene symbols", frac * 100));
        } else if (frac >= 0.35) {
            acc.add(ColumnType.GENE_NAME, 0.20, "moderate fraction of values resemble gene symbols");
        }
    }

    private void scoreStrandPattern(ScoreAccumulator acc) {
        long strandMatches = acc.nonEmpty.stream()
                .filter(v -> v.trim().matches("[+\\-\\.1]") || v.trim().matches("-1")
                        || v.trim().equalsIgnoreCase("plus") || v.trim().equalsIgnoreCase("minus")
                        || v.trim().equalsIgnoreCase("forward") || v.trim().equalsIgnoreCase("reverse"))
                .count();
        double frac = (double) strandMatches / acc.nonEmpty.size();
        long unique = acc.nonEmpty.stream().map(String::trim).distinct().count();

        if (frac >= 0.80 && unique <= 3) {
            acc.add(ColumnType.STRAND, 0.60, "values match strand indicators (+/-/.) with low cardinality");
        }
    }

    // ── Inner accumulator ──────────────────────────────────────────────────

    private static class ScoreAccumulator {
        final String header;
        final List<String> nonEmpty;
        String detectedGenome;

        private final Map<ColumnType, Double> typeScores = new EnumMap<>(ColumnType.class);
        private final List<String> reasonList = new ArrayList<>();
        private double boostScore = 0.0;

        ScoreAccumulator(String header, List<String> nonEmpty) {
            this.header = header;
            this.nonEmpty = nonEmpty;
        }

        void add(ColumnType type, double score, String reason) {
            typeScores.merge(type, score, Double::sum);
            reasonList.add(reason);
        }

        void boost(double score, String reason) {
            boostScore += score;
            reasonList.add(reason);
        }

        double totalScore() {
            return typeScores.values().stream().mapToDouble(Double::doubleValue).sum() + boostScore;
        }

        ColumnType dominantType() {
            return typeScores.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(ColumnType.UNKNOWN);
        }

        String reasons() {
            return String.join("; ", reasonList);
        }
    }
}
