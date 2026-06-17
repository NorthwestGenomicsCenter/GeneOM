package org.uw.nwgc.multiomics.util.coordinates_reader.model;

import java.util.Objects;

/**
 * Represents a single genomic coordinate/locus.
 * Supports both 0-based (BED-style) and 1-based (VCF-style) coordinate systems.
 */
public class GenomicCoordinate {

    public enum CoordinateSystem {
        ONE_BASED,   // VCF, GFF, GTF — start is inclusive, end is inclusive
        ZERO_BASED,  // BED — start is inclusive, end is exclusive
        UNKNOWN
    }

    private final String chromosome;       // Normalized: "1", "X", "MT", etc.
    private final Long start;              // null if point locus or chromosome-only
    private final Long end;                // null if point locus or chromosome-only
    private final CoordinateSystem coordinateSystem;
    private final String rawChromosome;    // Original value before normalization: "chr1", "Chr_1", etc.
    private final String sourceInterval;   // Original string representation, e.g. "1:236882298-236882307"

    private GenomicCoordinate(Builder builder) {
        this.chromosome = builder.chromosome;
        this.rawChromosome = builder.rawChromosome;
        this.start = builder.start;
        this.end = builder.end;
        this.coordinateSystem = builder.coordinateSystem != null
                ? builder.coordinateSystem : CoordinateSystem.UNKNOWN;
        this.sourceInterval = builder.sourceInterval;
    }

    public String getChromosome() { return chromosome; }
    public String getRawChromosome() { return rawChromosome; }
    public Long getStart() { return start; }
    public Long getEnd() { return end; }
    public CoordinateSystem getCoordinateSystem() { return coordinateSystem; }
    public String getSourceInterval() { return sourceInterval; }

    /** Returns true if this represents a ranged interval (has both start and end). */
    public boolean isInterval() { return start != null && end != null; }

    /** Returns true if this is a point position (start == end or only start is set). */
    public boolean isPoint() { return start != null && end == null; }

    /** Returns true if chromosome-only (no positional info). */
    public boolean isChromosomeOnly() { return start == null && end == null; }

    /**
     * Returns interval length. For ZERO_BASED: end - start.
     * For ONE_BASED: end - start + 1. Returns null if not an interval.
     */
    public Long getLength() {
        if (!isInterval()) return null;
        return coordinateSystem == CoordinateSystem.ZERO_BASED
                ? (end - start)
                : (end - start + 1);
    }

    @Override
    public String toString() {
        if (isChromosomeOnly()) return chromosome;
        if (isPoint()) return chromosome + ":" + start;
        return chromosome + ":" + start + "-" + end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenomicCoordinate)) return false;
        GenomicCoordinate that = (GenomicCoordinate) o;
        return Objects.equals(chromosome, that.chromosome)
                && Objects.equals(start, that.start)
                && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chromosome, start, end);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String chromosome;
        private String rawChromosome;
        private Long start;
        private Long end;
        private CoordinateSystem coordinateSystem;
        private String sourceInterval;

        public Builder chromosome(String chromosome) {
            this.chromosome = chromosome;
            return this;
        }
        public Builder rawChromosome(String rawChromosome) {
            this.rawChromosome = rawChromosome;
            return this;
        }
        public Builder start(Long start) { this.start = start; return this; }
        public Builder end(Long end) { this.end = end; return this; }
        public Builder coordinateSystem(CoordinateSystem cs) { this.coordinateSystem = cs; return this; }
        public Builder sourceInterval(String sourceInterval) {
            this.sourceInterval = sourceInterval;
            return this;
        }
        public GenomicCoordinate build() { return new GenomicCoordinate(this); }
    }
}
