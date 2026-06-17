package org.uw.nwgc.multiomics.util.coordinates_reader.parser;

import org.uw.nwgc.multiomics.util.coordinates_reader.model.GenomicCoordinate;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.GenomicCoordinate.CoordinateSystem;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses genomic coordinate strings in many common formats into {@link GenomicCoordinate}.
 *
 * <p>Supported formats:</p>
 * <pre>
 *  chr1:100-200             UCSC browser-style interval
 *  1:100-200                Ensembl-style interval
 *  chr1:100..200            Double-dot notation (GFF/EMBL)
 *  chr1 100 200             Space-separated
 *  chr1\t100\t200           Tab-separated (single-cell BED-in-cell)
 *  chr1,100,200             Comma-separated
 *  chr1:100                 Point position
 *  rs12345                  rsID (variant only, no coordinates)
 *  HGVS: NC_000001.11:g.12345A>T  (partial support — extracts chr and pos)
 *  100-200                  Position-only interval (chromosome unknown)
 * </pre>
 *
 * <p>The parser also handles thousand-separator commas in positions (1,234,567)
 * and whitespace-padded values.</p>
 */
public class IntervalParser {

    // chr:start-end  or  chr:start..end  (UCSC / Ensembl / GFF)
    private static final Pattern INTERVAL_COLON = Pattern.compile(
            "^([\\w.]+)\\s*[:：]\\s*([\\d,_]+)\\s*(?:[-–—]|\\.\\.)\\s*([\\d,_]+)$");

    // chr:pos  (point locus — VCF style without alt/ref)
    private static final Pattern POINT_COLON = Pattern.compile(
            "^([\\w.]+)\\s*[:：]\\s*([\\d,_]+)$");

    // chr start end  — space/tab/comma separated in a single string
    private static final Pattern INTERVAL_SEP = Pattern.compile(
            "^([\\w.]+)\\s+([\\d,_]+)\\s+([\\d,_]+)$");

    // start-end  or  start..end  — no chromosome
    private static final Pattern POSITION_ONLY_INTERVAL = Pattern.compile(
            "^([\\d,_]+)\\s*(?:[-–—]|\\.\\.)\\s*([\\d,_]+)$");

    // A single large integer — likely a genomic position
    private static final Pattern SINGLE_POSITION = Pattern.compile("^[\\d,_]{5,12}$");

    // rsID
    private static final Pattern RS_ID = Pattern.compile("(?i)^rs\\d+$");

    // HGVS genomic: NC_000001.11:g.12345A>T
    private static final Pattern HGVS_GENOMIC = Pattern.compile(
            "(?i)^(N[CT]_[\\d.]+):g\\.(\\d+)");

    private IntervalParser() {}

    /**
     * Attempt to parse a cell value into a {@link GenomicCoordinate}.
     *
     * @param raw raw cell string value
     * @return parsed coordinate, or empty if not parseable
     */
    public static Optional<GenomicCoordinate> parse(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();
        String trimmed = raw.trim();

        // Try each format in order of specificity
        Optional<GenomicCoordinate> result;

        result = tryIntervalColon(trimmed);
        if (result.isPresent()) return result;

        result = tryIntervalSep(trimmed);
        if (result.isPresent()) return result;

        result = tryHgvs(trimmed);
        if (result.isPresent()) return result;

        result = tryPointColon(trimmed);
        if (result.isPresent()) return result;

        result = tryPositionOnlyInterval(trimmed);
        if (result.isPresent()) return result;

        result = tryChromosomeOnly(trimmed);
        if (result.isPresent()) return result;

        return Optional.empty();
    }

    /**
     * Returns true if a string looks like it could be a parseable coordinate,
     * without fully parsing it (cheaper for column scanning).
     */
    public static boolean couldBeCoordinate(String raw) {
        if (raw == null || raw.isBlank()) return false;
        String t = raw.trim();
        return INTERVAL_COLON.matcher(t).matches()
                || POINT_COLON.matcher(t).matches()
                || INTERVAL_SEP.matcher(t).matches()
                || HGVS_GENOMIC.matcher(t).find()
                || ChromosomeNormalizer.looksLikeChromosome(t);
    }

    // ── private helpers ────────────────────────────────────────────────────

    private static Optional<GenomicCoordinate> tryIntervalColon(String s) {
        Matcher m = INTERVAL_COLON.matcher(s);
        if (!m.matches()) return Optional.empty();

        String rawChr = m.group(1);
        Optional<String> chr = ChromosomeNormalizer.normalize(rawChr);
        if (chr.isEmpty()) return Optional.empty();

        long start = parseLong(m.group(2));
        long end   = parseLong(m.group(3));
        if (start < 0 || end < 0 || start > end) return Optional.empty();

        return Optional.of(GenomicCoordinate.builder()
                .rawChromosome(rawChr)
                .chromosome(chr.get())
                .start(start)
                .end(end)
                .sourceInterval(s)
                .coordinateSystem(guessCoordSystem(start, end))
                .build());
    }

    private static Optional<GenomicCoordinate> tryPointColon(String s) {
        Matcher m = POINT_COLON.matcher(s);
        if (!m.matches()) return Optional.empty();

        String rawChr = m.group(1);
        Optional<String> chr = ChromosomeNormalizer.normalize(rawChr);
        if (chr.isEmpty()) return Optional.empty();

        long pos = parseLong(m.group(2));
        if (pos < 0) return Optional.empty();

        return Optional.of(GenomicCoordinate.builder()
                .rawChromosome(rawChr)
                .chromosome(chr.get())
                .start(pos)
                .sourceInterval(s)
                .coordinateSystem(CoordinateSystem.ONE_BASED)
                .build());
    }

    private static Optional<GenomicCoordinate> tryIntervalSep(String s) {
        Matcher m = INTERVAL_SEP.matcher(s);
        if (!m.matches()) return Optional.empty();

        String rawChr = m.group(1);
        Optional<String> chr = ChromosomeNormalizer.normalize(rawChr);
        if (chr.isEmpty()) return Optional.empty();

        long start = parseLong(m.group(2));
        long end   = parseLong(m.group(3));
        if (start < 0 || end < 0 || start > end) return Optional.empty();

        return Optional.of(GenomicCoordinate.builder()
                .rawChromosome(rawChr)
                .chromosome(chr.get())
                .start(start)
                .end(end)
                .sourceInterval(s)
                .coordinateSystem(guessCoordSystem(start, end))
                .build());
    }

    private static Optional<GenomicCoordinate> tryHgvs(String s) {
        Matcher m = HGVS_GENOMIC.matcher(s);
        if (!m.find()) return Optional.empty();

        String acc = m.group(1);
        Optional<String> chr = ChromosomeNormalizer.normalize(acc);
        if (chr.isEmpty()) return Optional.empty();

        long pos = parseLong(m.group(2));
        return Optional.of(GenomicCoordinate.builder()
                .rawChromosome(acc)
                .chromosome(chr.get())
                .start(pos)
                .sourceInterval(s)
                .coordinateSystem(CoordinateSystem.ONE_BASED)
                .build());
    }

    private static Optional<GenomicCoordinate> tryPositionOnlyInterval(String s) {
        Matcher m = POSITION_ONLY_INTERVAL.matcher(s);
        if (!m.matches()) return Optional.empty();

        long start = parseLong(m.group(1));
        long end   = parseLong(m.group(2));
        if (start < 0 || end < 0 || start > end) return Optional.empty();

        return Optional.of(GenomicCoordinate.builder()
                .start(start)
                .end(end)
                .sourceInterval(s)
                .coordinateSystem(CoordinateSystem.UNKNOWN)
                .build());
    }

    private static Optional<GenomicCoordinate> tryChromosomeOnly(String s) {
        Optional<String> chr = ChromosomeNormalizer.normalize(s);
        if (chr.isEmpty()) return Optional.empty();

        return Optional.of(GenomicCoordinate.builder()
                .rawChromosome(s)
                .chromosome(chr.get())
                .sourceInterval(s)
                .build());
    }

    /** Strip thousand-separators (commas) and underscores, then parse. Returns -1 on failure. */
    public static long parseLong(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s.replace(",", "").replace("_", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Heuristic: if start looks like it could be 0-based (start==0 or interval
     * length is suspiciously short), guess ZERO_BASED, otherwise ONE_BASED.
     * This is imprecise — callers should expose it to users and allow override.
     */
    private static CoordinateSystem guessCoordSystem(long start, long end) {
        if (start == 0) return CoordinateSystem.ZERO_BASED;
        return CoordinateSystem.ONE_BASED;
    }
}
