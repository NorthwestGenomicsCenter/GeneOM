package org.uw.nwgc.multiomics.util.coordinates_reader.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Normalizes chromosome identifiers from various naming conventions to a
 * canonical form (no "chr" prefix, uppercase letter chromosomes).
 *
 * <p>Handles:</p>
 * <ul>
 *   <li>UCSC style: chr1, chrX, chrMT, chrUn_gl000220</li>
 *   <li>Ensembl/NCBI style: 1, X, MT</li>
 *   <li>NCBI RefSeq accessions: NC_000001.11, etc.</li>
 *   <li>Mitochondrial aliases: M, MT, chrM, chrMT, mito</li>
 *   <li>Legacy aliases: chr23=X, chr24=Y (some older tools)</li>
 *   <li>Drosophila, mouse, zebrafish common patterns</li>
 *   <li>Patch/scaffold contigs: chr1_gl000191_random, etc.</li>
 * </ul>
 */
public class ChromosomeNormalizer {

    // Canonical valid chromosome names (human GRCh37/38 + common aliases)
    public static final Set<String> HUMAN_AUTOSOMES;
    public static final Set<String> HUMAN_SEX_CHROMS = Set.of("X", "Y");
    public static final Set<String> HUMAN_SPECIAL = Set.of("MT", "M");

    static {
        var auto = new java.util.HashSet<String>();
        for (int i = 1; i <= 22; i++) auto.add(String.valueOf(i));
        HUMAN_AUTOSOMES = Set.copyOf(auto);
    }

    /** Maps known alternate names → canonical name */
    private static final Map<String, String> ALIAS_MAP = new HashMap<>();

    static {
        // Mitochondrial aliases
        ALIAS_MAP.put("M", "MT");
        ALIAS_MAP.put("CHRM", "MT");
        ALIAS_MAP.put("CHRMT", "MT");
        ALIAS_MAP.put("MITO", "MT");
        ALIAS_MAP.put("MITOCHONDRIA", "MT");
        ALIAS_MAP.put("MITOCHONDRIAL", "MT");

        // Legacy numeric sex chromosome aliases (some older PLINK-style files)
        ALIAS_MAP.put("23", "X");
        ALIAS_MAP.put("24", "Y");
        ALIAS_MAP.put("25", "XY"); // pseudoautosomal
        ALIAS_MAP.put("26", "MT");

        // Explicit chr-prefix sex/mito
        ALIAS_MAP.put("CHRX", "X");
        ALIAS_MAP.put("CHRY", "Y");
        ALIAS_MAP.put("CHRXY", "XY");

        // Roman numeral chromosomes (yeast, some legacy datasets)
        String[] romans = {"I","II","III","IV","V","VI","VII","VIII","IX","X",
                           "XI","XII","XIII","XIV","XV","XVI"};
        for (int i = 0; i < romans.length; i++) {
            ALIAS_MAP.put("CHR" + romans[i], String.valueOf(i + 1));
        }
    }

    private static final Pattern CHR_PREFIX      = Pattern.compile("^[Cc][Hh][Rr][_\\-]?(.+)$");
    private static final Pattern REFSEQ_ACC      = Pattern.compile("^N[CT]_\\d+\\.?\\d*$");
    private static final Pattern SCAFFOLD_CONTIG = Pattern.compile("(?i)^(chr)?[\\w]+_(?:random|alt|fix|hap\\d+|gl\\d+|ki\\d+)");
    private static final Pattern NUMERIC_ONLY    = Pattern.compile("^\\d{1,2}$");

    // Approximate RefSeq → chromosome mappings (hg38)
    private static final Map<String, String> REFSEQ_MAP = buildRefSeqMap();

    private ChromosomeNormalizer() {}

    /**
     * Normalize a raw chromosome string to canonical form.
     * Returns empty if the string cannot be recognized as a chromosome identifier.
     *
     * @param raw raw chromosome string (e.g. "chr1", "chrMT", "NC_000001.11", "X")
     * @return normalized chromosome (e.g. "1", "MT", "X") or empty Optional
     */
    public static Optional<String> normalize(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();

        String trimmed = raw.trim();

        // RefSeq accession shortcut
        if (REFSEQ_ACC.matcher(trimmed).matches()) {
            String mapped = REFSEQ_MAP.get(trimmed.toUpperCase());
            return Optional.ofNullable(mapped);
        }

        // Strip chr prefix
        String working = trimmed;
        var chrMatcher = CHR_PREFIX.matcher(trimmed);
        if (chrMatcher.matches()) {
            working = chrMatcher.group(1);
        }

        String upper = working.toUpperCase();

        // Check alias table first (handles M→MT, 23→X, etc.)
        String aliasKey = chrMatcher.matches() ? "CHR" + upper : upper;
        if (ALIAS_MAP.containsKey(aliasKey)) return Optional.of(ALIAS_MAP.get(aliasKey));
        if (ALIAS_MAP.containsKey(upper))    return Optional.of(ALIAS_MAP.get(upper));

        // Valid human autosome
        if (NUMERIC_ONLY.matcher(working).matches()) {
            int n = Integer.parseInt(working);
            if (n >= 1 && n <= 22) return Optional.of(String.valueOf(n));
            return Optional.empty();
        }

        // Sex/mito single letters
        if (upper.equals("X") || upper.equals("Y")) return Optional.of(upper);
        if (upper.equals("MT") || upper.equals("M")) return Optional.of("MT");

        // Scaffold/patch/contig — return as-is (valid but non-standard)
        if (SCAFFOLD_CONTIG.matcher(trimmed).find()) {
            return Optional.of(trimmed); // preserve full name for scaffolds
        }

        return Optional.empty();
    }

    /**
     * Returns true if the string looks like any chromosome identifier
     * (even if normalization fails — e.g. a zebrafish chr1 is still "chromosome-like").
     */
    public static boolean looksLikeChromosome(String raw) {
        if (raw == null || raw.isBlank()) return false;
        if (normalize(raw).isPresent()) return true;
        // Accept "chr" + anything as loosely chromosome-like
        return CHR_PREFIX.matcher(raw.trim()).matches();
    }

    /**
     * Returns the genome assembly hint if the string contains one, e.g.
     * "GRCh38" from "GRCh38 Interval" column header.
     */
    public static Optional<String> detectAssemblyHint(String text) {
        if (text == null) return Optional.empty();
        String upper = text.toUpperCase();
        if (upper.contains("GRCH38") || upper.contains("HG38")) return Optional.of("GRCh38");
        if (upper.contains("GRCH37") || upper.contains("HG19")) return Optional.of("GRCh37");
        if (upper.contains("GRCH36") || upper.contains("HG18")) return Optional.of("GRCh36");
        if (upper.contains("MM10") || upper.contains("GRCM38")) return Optional.of("mm10");
        if (upper.contains("MM39") || upper.contains("GRCM39")) return Optional.of("mm39");
        return Optional.empty();
    }

    private static Map<String, String> buildRefSeqMap() {
        // GRCh38 NC_ accessions → chromosome
        Map<String, String> m = new HashMap<>();
        String[] acc = {
            "NC_000001","NC_000002","NC_000003","NC_000004","NC_000005","NC_000006",
            "NC_000007","NC_000008","NC_000009","NC_000010","NC_000011","NC_000012",
            "NC_000013","NC_000014","NC_000015","NC_000016","NC_000017","NC_000018",
            "NC_000019","NC_000020","NC_000021","NC_000022"
        };
        for (int i = 0; i < acc.length; i++) {
            m.put(acc[i], String.valueOf(i + 1));
            // Accept with or without version suffix (.10, .11, etc.)
            for (int v = 1; v <= 15; v++) m.put(acc[i] + "." + v, String.valueOf(i + 1));
        }
        // Sex + MT
        for (int v = 1; v <= 15; v++) {
            m.put("NC_000023." + v, "X");
            m.put("NC_000024." + v, "Y");
            m.put("NC_012920." + v, "MT");
        }
        m.put("NC_000023", "X");
        m.put("NC_000024", "Y");
        m.put("NC_012920", "MT");
        return Map.copyOf(m);
    }
}
