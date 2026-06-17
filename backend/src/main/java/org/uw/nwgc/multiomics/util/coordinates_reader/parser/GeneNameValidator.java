package org.uw.nwgc.multiomics.util.coordinates_reader.parser;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Heuristics for recognizing gene names, gene IDs, transcript IDs, and variant IDs.
 *
 * <p>Gene symbol validation follows HGNC conventions (https://www.genenames.org/about/guidelines/)
 * but must be purely pattern-based here — a full HGNC lookup would require network access.</p>
 */
public class GeneNameValidator {

    // ── Pattern constants ──────────────────────────────────────────────────

    // HGNC gene symbol: 1–8 uppercase alphanumeric chars, may start with letter,
    // may contain hyphens (e.g. HLA-A, MIR-21, C1orf56)
    private static final Pattern HGNC_SYMBOL = Pattern.compile(
            "^[A-Z][A-Z0-9]{0,7}(?:[-][A-Z0-9]{1,6})*(?:\\d+)?$");

    // More permissive: some symbols include "." (e.g. CEL.2, OTTHUMG etc.)
    private static final Pattern GENE_SYMBOL_RELAXED = Pattern.compile(
            "^[A-Z][A-Z0-9.\\-]{0,14}$");

    // Ensembl gene ID: ENSG00000xxxxxx (human), ENSMUSG... (mouse), ENSDARG... (zebrafish), etc.
    private static final Pattern ENSEMBL_GENE = Pattern.compile(
            "^ENS[A-Z]{0,4}G\\d{11}(?:\\.\\d+)?$");

    // Ensembl transcript ID
    private static final Pattern ENSEMBL_TRANSCRIPT = Pattern.compile(
            "^ENS[A-Z]{0,4}T\\d{11}(?:\\.\\d+)?$");

    // Ensembl protein ID
    private static final Pattern ENSEMBL_PROTEIN = Pattern.compile(
            "^ENS[A-Z]{0,4}P\\d{11}(?:\\.\\d+)?$");

    // Entrez/NCBI Gene ID — a plain integer, but we need context to distinguish from coords
    // This pattern alone is too broad; use alongside column-name hints.
    private static final Pattern ENTREZ_ID = Pattern.compile("^\\d{1,9}$");

    // RefSeq mRNA: NM_xxxxxx, XM_xxxxxx
    private static final Pattern REFSEQ_MRNA = Pattern.compile(
            "^[NX]M_\\d{6,9}(?:\\.\\d+)?$");

    // RefSeq protein: NP_xxxxxx
    private static final Pattern REFSEQ_PROTEIN = Pattern.compile(
            "^[NX]P_\\d{6,9}(?:\\.\\d+)?$");

    // RefSeq genomic
    private static final Pattern REFSEQ_GENOMIC = Pattern.compile(
            "^N[CTW]_\\d{6,9}(?:\\.\\d+)?$");

    // dbSNP rsID
    private static final Pattern RS_ID = Pattern.compile("(?i)^rs\\d+$");

    // ClinVar variant ID
    private static final Pattern CLINVAR_ID = Pattern.compile("(?i)^(RCV|VCV)\\d{9}(?:\\.\\d+)?$");

    // HGVS (rough): contains ":" and ">", "del", "ins", "dup", "fs"
    private static final Pattern HGVS_VARIANT = Pattern.compile(
            "(?i).+:.+(?:[>]|del|ins|dup|fs|inv|con)");

    // UniProt accession (P12345, Q1234A, O00001-2, etc.)
    private static final Pattern UNIPROT = Pattern.compile(
            "^[OPQ][0-9][A-Z0-9]{3}[0-9](?:-\\d+)?$|^[A-NR-Z][0-9](?:[A-Z][A-Z0-9]{2}[0-9]){1,2}(?:-\\d+)?$");

    // Words that look like gene symbols but are almost certainly NOT genes
    private static final Set<String> SYMBOL_BLACKLIST = Set.of(
            "TRUE", "FALSE", "NA", "NAN", "NULL", "N/A", "YES", "NO",
            "CHR", "POS", "REF", "ALT", "PASS", "FAIL", "INFO", "QUAL",
            "START", "END", "STOP", "GENE", "NAME", "ID", "TYPE",
            "SAMPLE", "CASE", "CONTROL", "WT", "MUT"
    );

    private GeneNameValidator() {}

    public enum GeneIdType {
        HGNC_SYMBOL,
        ENSEMBL_GENE,
        ENSEMBL_TRANSCRIPT,
        ENSEMBL_PROTEIN,
        REFSEQ_MRNA,
        REFSEQ_PROTEIN,
        REFSEQ_GENOMIC,
        ENTREZ_ID,
        RS_ID,
        CLINVAR_ID,
        HGVS,
        UNIPROT,
        UNKNOWN
    }

    public record GeneIdMatch(GeneIdType type, String normalized) {}

    /**
     * Classify a cell value as a gene/variant identifier if possible.
     *
     * @param raw raw cell string
     * @return match record if recognized, empty otherwise
     */
    public static Optional<GeneIdMatch> classify(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();
        String t = raw.trim();

        if (RS_ID.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.RS_ID, t.toLowerCase()));

        if (CLINVAR_ID.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.CLINVAR_ID, t.toUpperCase()));

        if (HGVS_VARIANT.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.HGVS, t));

        if (ENSEMBL_GENE.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.ENSEMBL_GENE, t));

        if (ENSEMBL_TRANSCRIPT.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.ENSEMBL_TRANSCRIPT, t));

        if (ENSEMBL_PROTEIN.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.ENSEMBL_PROTEIN, t));

        if (REFSEQ_MRNA.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.REFSEQ_MRNA, t));

        if (REFSEQ_PROTEIN.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.REFSEQ_PROTEIN, t));

        if (REFSEQ_GENOMIC.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.REFSEQ_GENOMIC, t));

        if (UNIPROT.matcher(t).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.UNIPROT, t));

        // HGNC symbol — most ambiguous; only accept if not blacklisted
        String upper = t.toUpperCase();
        if (!SYMBOL_BLACKLIST.contains(upper) && HGNC_SYMBOL.matcher(upper).matches())
            return Optional.of(new GeneIdMatch(GeneIdType.HGNC_SYMBOL, upper));

        return Optional.empty();
    }

    /**
     * Whether a string *strongly* looks like a gene symbol (HGNC-style),
     * as opposed to a general identifier.
     */
    public static boolean isLikelyGeneSymbol(String raw) {
        if (raw == null || raw.isBlank()) return false;
        String upper = raw.trim().toUpperCase();
        return !SYMBOL_BLACKLIST.contains(upper)
                && HGNC_SYMBOL.matcher(upper).matches()
                && upper.length() >= 2
                && upper.length() <= 10;
    }

    /**
     * Whether a column header name suggests it contains gene names.
     */
    public static boolean isGeneHeader(String header) {
        if (header == null) return false;
        String lower = header.trim().toLowerCase();
        return lower.equals("gene") || lower.equals("gene_name") || lower.equals("gene name")
                || lower.equals("genesymbol") || lower.equals("gene_symbol") || lower.equals("gene symbol")
                || lower.equals("hgnc") || lower.equals("hgnc_symbol") || lower.equals("symbol")
                || lower.equals("gene_id") || lower.equals("geneid") || lower.equals("entrez")
                || lower.equals("entrez_id") || lower.equals("ncbi_gene") || lower.equals("ncbi gene")
                || lower.contains("ensembl") || lower.contains("transcript")
                || lower.contains("uniprot") || lower.contains("refseq")
                || lower.equals("hugo") || lower.equals("approved_symbol");
    }
}
