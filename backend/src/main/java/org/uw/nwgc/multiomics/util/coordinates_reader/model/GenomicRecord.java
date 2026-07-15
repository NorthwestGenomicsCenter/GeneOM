package org.uw.nwgc.multiomics.util.coordinates_reader.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A single data row resolved into typed genomic fields.
 * <p>
 * The coordinate may be assembled from one column (interval format like "chr1:100-200")
 * or from separate chromosome/start/end columns. Raw cell values for every column
 * are preserved for downstream use.
 * </p>
 */
public class GenomicRecord {

    private final int rowIndex;                     // 0-based row index (excluding header)
    private final GenomicCoordinate coordinate;     // Primary resolved coordinate; null if unparseable
    private final String geneName;                  // Resolved gene name/symbol, if any
    private final String geneId;                    // Ensembl/Entrez/etc. ID, if any
    private final String transcriptId;
    private final String variantId;                 // rsID, HGVS, etc.
    private final String strand;
    private final Map<String, Object> rawValues;    // All original column→value pairs
    private final Map<String, String> parseWarnings;// columnName → warning message

    private GenomicRecord(Builder builder) {
        this.rowIndex = builder.rowIndex;
        this.coordinate = builder.coordinate;
        this.geneName = builder.geneName;
        this.geneId = builder.geneId;
        this.transcriptId = builder.transcriptId;
        this.variantId = builder.variantId;
        this.strand = builder.strand;
        this.rawValues = new LinkedHashMap<>(builder.rawValues);
        this.parseWarnings = Collections.unmodifiableMap(new LinkedHashMap<>(builder.parseWarnings));
    }

    public int getRowIndex()                            { return rowIndex; }
    public Optional<GenomicCoordinate> getCoordinate()  { return Optional.ofNullable(coordinate); }
    public Optional<String> getGeneName()               { return Optional.ofNullable(geneName); }
    public Optional<String> getGeneId()                 { return Optional.ofNullable(geneId); }
    public Optional<String> getTranscriptId()           { return Optional.ofNullable(transcriptId); }
    public Optional<String> getVariantId()              { return Optional.ofNullable(variantId); }
    public Optional<String> getStrand()                 { return Optional.ofNullable(strand); }
    public Map<String, Object> getRawValues()           { return rawValues; }
    public Map<String, String> getParseWarnings()       { return parseWarnings; }

    /** True if the row produced any usable genomic data. */
    public boolean hasAnyGenomicData() {
        return coordinate != null || geneName != null || geneId != null
                || transcriptId != null || variantId != null;
    }

    /** True if coordinate is present AND has at minimum a chromosome. */
    public boolean hasCoordinate() {
        return coordinate != null;
    }

    @Override
    public String toString() {
        return String.format("GenomicRecord{row=%d, coord=%s, gene=%s, id=%s}",
                rowIndex, coordinate, geneName, geneId);
    }

    public static Builder builder(int rowIndex) { return new Builder(rowIndex); }

    public static class Builder {
        private final int rowIndex;
        GenomicCoordinate coordinate;  // package-private for RecordAssembler
        private String geneName;
        private String geneId;
        private String transcriptId;
        private String variantId;
        private String strand;
        private final Map<String, String> rawValues = new LinkedHashMap<>();
        private final Map<String, String> parseWarnings = new LinkedHashMap<>();

        Builder(int rowIndex) { this.rowIndex = rowIndex; }

        public Builder coordinate(GenomicCoordinate c)      { this.coordinate = c; return this; }
        public boolean hasCoordinate()                       { return this.coordinate != null; }
        public Builder geneName(String g)                    { this.geneName = g; return this; }
        public Builder geneId(String id)                    { this.geneId = id; return this; }
        public Builder transcriptId(String t)               { this.transcriptId = t; return this; }
        public Builder variantId(String v)                  { this.variantId = v; return this; }
        public Builder strand(String s)                     { this.strand = s; return this; }
        public Builder rawValue(String col, String val)     { rawValues.put(col, val); return this; }
        public Builder rawValues(Map<String, String> m)     { rawValues.putAll(m); return this; }
        public Builder warning(String col, String msg)      { parseWarnings.put(col, msg); return this; }
        public GenomicRecord build()                        { return new GenomicRecord(this); }
    }
}
