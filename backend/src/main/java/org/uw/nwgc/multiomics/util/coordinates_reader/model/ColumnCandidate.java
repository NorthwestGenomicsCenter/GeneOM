package org.uw.nwgc.multiomics.util.coordinates_reader.model;

import java.util.List;
import java.util.Objects;

/**
 * Result of attempting to identify a genomic column in a spreadsheet.
 * Carries confidence scoring, column type classification, and sample values.
 */
public class ColumnCandidate {

    /**
     * What kind of genomic information this column likely contains.
     */
    public enum ColumnType {
        INTERVAL,           // Combined chr:start-end or chr\tstart\tend in one cell
        CHROMOSOME,         // Chromosome identifier only (chr1, X, chrMT, etc.)
        START_POSITION,     // Genomic start coordinate
        END_POSITION,       // Genomic end coordinate
        POSITION,           // Single-position SNP/variant column (no start/end distinction)
        GENE_NAME,          // HGNC gene symbol (BRCA1, TP53, etc.)
        GENE_ID,            // Ensembl (ENSG...), Entrez, or other gene ID
        TRANSCRIPT_ID,      // ENST..., NM_..., etc.
        VARIANT_ID,         // rsID (rs12345), HGVS notation, etc.
        STRAND,             // +/- or 1/-1
        UNKNOWN
    }

    /** Confidence level buckets for human-readable summary */
    public enum Confidence {
        HIGH,       // >= 0.75 — strongly matches known patterns
        MEDIUM,     // >= 0.40 — likely but ambiguous
        LOW,        // >= 0.15 — weak signal
        VERY_LOW    // < 0.15  — speculative
    }

    private final int columnIndex;          // 0-based column index in source file
    private final String headerName;        // Raw header string (may be null if no header row)
    private final ColumnType columnType;
    private final double score;             // 0.0–1.0 composite confidence score
    private final List<String> sampleValues;
    private final String detectionReason;   // Human-readable explanation of why this was flagged
    private final String genome;            // "GRCh37", "GRCh38", "hg19", "mm10", etc. — if detectable

    private ColumnCandidate(Builder builder) {
        this.columnIndex = builder.columnIndex;
        this.headerName = builder.headerName;
        this.columnType = builder.columnType;
        this.score = builder.score;
        this.sampleValues = builder.sampleValues != null ? List.copyOf(builder.sampleValues) : List.of();
        this.detectionReason = builder.detectionReason;
        this.genome = builder.genome;
    }

    public int getColumnIndex()          { return columnIndex; }
    public String getHeaderName()        { return headerName; }
    public ColumnType getColumnType()    { return columnType; }
    public double getScore()             { return score; }
    public List<String> getSampleValues(){ return sampleValues; }
    public String getDetectionReason()   { return detectionReason; }
    public String getGenome()            { return genome; }

    public Confidence getConfidence() {
        if (score >= 0.75) return Confidence.HIGH;
        if (score >= 0.40) return Confidence.MEDIUM;
        if (score >= 0.15) return Confidence.LOW;
        return Confidence.VERY_LOW;
    }

    /** Display name: uses header if present, otherwise "Column N" (1-based for human readers). */
    public String getDisplayName() {
        return headerName != null && !headerName.isBlank()
                ? "\"" + headerName + "\" (col " + (columnIndex + 1) + ")"
                : "Column " + (columnIndex + 1);
    }

    @Override
    public String toString() {
        return String.format("ColumnCandidate{col=%d, header='%s', type=%s, score=%.2f [%s], reason='%s'}",
                columnIndex, headerName, columnType, score, getConfidence(), detectionReason);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColumnCandidate)) return false;
        ColumnCandidate that = (ColumnCandidate) o;
        return columnIndex == that.columnIndex && Objects.equals(headerName, that.headerName);
    }

    @Override
    public int hashCode() { return Objects.hash(columnIndex, headerName); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int columnIndex;
        private String headerName;
        private ColumnType columnType = ColumnType.UNKNOWN;
        private double score;
        private List<String> sampleValues;
        private String detectionReason;
        private String genome;

        public Builder columnIndex(int i)             { this.columnIndex = i; return this; }
        public Builder headerName(String n)           { this.headerName = n; return this; }
        public Builder columnType(ColumnType t)       { this.columnType = t; return this; }
        public Builder score(double s)                { this.score = Math.max(0, Math.min(1, s)); return this; }
        public Builder sampleValues(List<String> sv)  { this.sampleValues = sv; return this; }
        public Builder detectionReason(String r)      { this.detectionReason = r; return this; }
        public Builder genome(String g)               { this.genome = g; return this; }
        public ColumnCandidate build()                { return new ColumnCandidate(this); }
    }
}
