package org.uw.nwgc.multiomics.util.coordinates_reader.detector;

import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate.ColumnType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Post-processes a list of {@link ColumnCandidate}s to resolve cross-column
 * relationships, such as identifying a (chromosome, start, end) trio that
 * together form a BED-style genomic interval.
 *
 * <p>Also handles:</p>
 * <ul>
 *   <li>Boosting start/end columns that are adjacent to a chromosome column</li>
 *   <li>Detecting paired interval columns (e.g. GRCh37 Interval + GRCh38 Interval)</li>
 *   <li>Penalizing position columns that are adjacent to a column that looks like
 *       a gene ID (high co-occurrence of non-positional data)</li>
 *   <li>Ranking and deduplication of overlapping candidates</li>
 * </ul>
 */
public class ColumnGroupResolver {

    /**
     * A resolved group of columns that together describe a genomic locus.
     */
    public static class ColumnGroup {
        public enum GroupType { BED_TRIO, INTERVAL_SINGLE, INTERVAL_PAIR, POSITION_ONLY, GENE_ONLY }

        public final GroupType groupType;
        /** The primary/best-scoring candidate in this group. */
        public final ColumnCandidate primary;
        /** Supporting columns (e.g. start+end in a BED trio; secondary assembly interval). */
        public final List<ColumnCandidate> supporting;
        public final double groupScore;
        public final String description;

        ColumnGroup(GroupType t, ColumnCandidate primary, List<ColumnCandidate> supporting,
                    double score, String description) {
            this.groupType = t;
            this.primary = primary;
            this.supporting = List.copyOf(supporting);
            this.groupScore = score;
            this.description = description;
        }

        /** All columns in this group, primary first. */
        public List<ColumnCandidate> allColumns() {
            List<ColumnCandidate> all = new ArrayList<>();
            all.add(primary);
            all.addAll(supporting);
            return Collections.unmodifiableList(all);
        }

        @Override
        public String toString() {
            return String.format("ColumnGroup{type=%s, score=%.2f, primary=%s, description='%s'}",
                    groupType, groupScore, primary.getDisplayName(), description);
        }
    }

    /**
     * Resolve cross-column relationships from a flat list of candidates.
     *
     * <p>The returned list is sorted by descending group score. Columns that are
     * consumed into a higher-confidence group are excluded from lower-scoring groups.</p>
     *
     * @param candidates scored candidates from {@link ColumnScorer}
     * @return resolved groups, sorted by descending confidence
     */
    public List<ColumnGroup> resolve(List<ColumnCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) return List.of();

        List<ColumnGroup> groups = new ArrayList<>();
        Set<Integer> consumed = new HashSet<>();

        // 1. BED trios: chromosome + adjacent start + adjacent end
        resolveBedTrios(candidates, groups, consumed);

        // 2. Single interval columns (chr:start-end in one cell)
        resolveSingleIntervals(candidates, groups, consumed);

        // 3. Paired interval columns (e.g. GRCh37 + GRCh38 side by side)
        resolveIntervalPairs(candidates, groups, consumed);

        // 4. Remaining position columns
        resolvePositionOnly(candidates, groups, consumed);

        // 5. Gene columns (no coordinate info)
        resolveGeneOnly(candidates, groups, consumed);

        // Sort by descending score
        groups.sort(Comparator.comparingDouble((ColumnGroup g) -> g.groupScore).reversed());
        return Collections.unmodifiableList(groups);
    }

    // ── Resolution strategies ──────────────────────────────────────────────

    private void resolveBedTrios(List<ColumnCandidate> candidates, List<ColumnGroup> groups, Set<Integer> consumed) {
        List<ColumnCandidate> chrCols   = byType(candidates, ColumnType.CHROMOSOME);
        List<ColumnCandidate> startCols = byType(candidates, ColumnType.START_POSITION);
        List<ColumnCandidate> endCols   = byType(candidates, ColumnType.END_POSITION);

        // Try to pair each chromosome column with the nearest start and end
        for (ColumnCandidate chr : chrCols) {
            if (consumed.contains(chr.getColumnIndex())) continue;

            Optional<ColumnCandidate> start = findNearest(chr, startCols, consumed);
            Optional<ColumnCandidate> end   = findNearest(chr, endCols, consumed);

            if (start.isPresent() && end.isPresent()) {
                double score = (chr.getScore() + start.get().getScore() + end.get().getScore()) / 3.0
                        + adjacencyBonus(chr, start.get(), end.get());

                String desc = String.format("BED-style trio: chromosome=%s, start=%s, end=%s",
                        chr.getDisplayName(), start.get().getDisplayName(), end.get().getDisplayName());

                groups.add(new ColumnGroup(
                        ColumnGroup.GroupType.BED_TRIO, chr,
                        List.of(start.get(), end.get()),
                        Math.min(1.0, score), desc));

                consumed.add(chr.getColumnIndex());
                consumed.add(start.get().getColumnIndex());
                consumed.add(end.get().getColumnIndex());
            }
        }
    }

    private void resolveSingleIntervals(List<ColumnCandidate> candidates, List<ColumnGroup> groups, Set<Integer> consumed) {
        for (ColumnCandidate c : candidates) {
            if (consumed.contains(c.getColumnIndex())) continue;
            if (c.getColumnType() != ColumnType.INTERVAL) continue;

            String desc = "Single-column interval: " + c.getDisplayName();
            if (c.getGenome() != null) desc += " (" + c.getGenome() + ")";

            groups.add(new ColumnGroup(
                    ColumnGroup.GroupType.INTERVAL_SINGLE, c, List.of(),
                    c.getScore(), desc));
            consumed.add(c.getColumnIndex());
        }
    }

    private void resolveIntervalPairs(List<ColumnCandidate> candidates, List<ColumnGroup> groups, Set<Integer> consumed) {
        // Look for pairs of interval columns with different assembly annotations
        List<ColumnCandidate> intervalCols = byType(candidates, ColumnType.INTERVAL).stream()
                .filter(c -> !consumed.contains(c.getColumnIndex()))
                .collect(Collectors.toList());

        for (int i = 0; i < intervalCols.size(); i++) {
            for (int j = i + 1; j < intervalCols.size(); j++) {
                ColumnCandidate a = intervalCols.get(i);
                ColumnCandidate b = intervalCols.get(j);
                if (consumed.contains(a.getColumnIndex()) || consumed.contains(b.getColumnIndex())) continue;

                boolean differentAssemblies = a.getGenome() != null && b.getGenome() != null
                        && !a.getGenome().equals(b.getGenome());
                boolean adjacent = Math.abs(a.getColumnIndex() - b.getColumnIndex()) <= 2;

                if (differentAssemblies || adjacent) {
                    double score = (a.getScore() + b.getScore()) / 2.0 + (differentAssemblies ? 0.15 : 0.05);
                    String desc = String.format("Interval pair: %s / %s%s",
                            a.getDisplayName(), b.getDisplayName(),
                            differentAssemblies ? " [different assemblies: " + a.getGenome() + " vs " + b.getGenome() + "]" : "");

                    groups.add(new ColumnGroup(
                            ColumnGroup.GroupType.INTERVAL_PAIR, a, List.of(b),
                            Math.min(1.0, score), desc));
                    consumed.add(a.getColumnIndex());
                    consumed.add(b.getColumnIndex());
                }
            }
        }
    }

    private void resolvePositionOnly(List<ColumnCandidate> candidates, List<ColumnGroup> groups, Set<Integer> consumed) {
        for (ColumnCandidate c : candidates) {
            if (consumed.contains(c.getColumnIndex())) continue;
            if (c.getColumnType() != ColumnType.POSITION
                    && c.getColumnType() != ColumnType.START_POSITION
                    && c.getColumnType() != ColumnType.END_POSITION) continue;

            groups.add(new ColumnGroup(
                    ColumnGroup.GroupType.POSITION_ONLY, c, List.of(),
                    c.getScore(), "Standalone position column: " + c.getDisplayName()));
            consumed.add(c.getColumnIndex());
        }
    }

    private void resolveGeneOnly(List<ColumnCandidate> candidates, List<ColumnGroup> groups, Set<Integer> consumed) {
        for (ColumnCandidate c : candidates) {
            if (consumed.contains(c.getColumnIndex())) continue;
            if (c.getColumnType() == ColumnType.GENE_NAME
                    || c.getColumnType() == ColumnType.GENE_ID
                    || c.getColumnType() == ColumnType.TRANSCRIPT_ID
                    || c.getColumnType() == ColumnType.VARIANT_ID) {
                groups.add(new ColumnGroup(
                        ColumnGroup.GroupType.GENE_ONLY, c, List.of(),
                        c.getScore(), "Gene/ID column: " + c.getDisplayName()));
                consumed.add(c.getColumnIndex());
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private List<ColumnCandidate> byType(List<ColumnCandidate> candidates, ColumnType type) {
        return candidates.stream()
                .filter(c -> c.getColumnType() == type)
                .sorted(Comparator.comparingDouble(ColumnCandidate::getScore).reversed())
                .collect(Collectors.toList());
    }

    /** Find the candidate nearest (by column index) to the anchor, excluding consumed. */
    private Optional<ColumnCandidate> findNearest(ColumnCandidate anchor, List<ColumnCandidate> pool, Set<Integer> consumed) {
        return pool.stream()
                .filter(c -> !consumed.contains(c.getColumnIndex()))
                .min(Comparator.comparingInt(c -> Math.abs(c.getColumnIndex() - anchor.getColumnIndex())));
    }

    /** Bonus for chr/start/end being adjacent in column order. */
    private double adjacencyBonus(ColumnCandidate chr, ColumnCandidate start, ColumnCandidate end) {
        int chrIdx   = chr.getColumnIndex();
        int startIdx = start.getColumnIndex();
        int endIdx   = end.getColumnIndex();

        // Classic BED order: chr, start, end (columns 0, 1, 2)
        if (startIdx == chrIdx + 1 && endIdx == chrIdx + 2) return 0.20;
        // Adjacent but not consecutive
        if (Math.abs(startIdx - chrIdx) <= 2 && Math.abs(endIdx - chrIdx) <= 3) return 0.10;
        return 0.0;
    }
}
