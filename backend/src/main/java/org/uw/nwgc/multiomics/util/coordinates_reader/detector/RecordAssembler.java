package org.uw.nwgc.multiomics.util.coordinates_reader.detector;

import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate.ColumnType;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.GenomicCoordinate;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.GenomicRecord;
import org.uw.nwgc.multiomics.util.coordinates_reader.parser.ChromosomeNormalizer;
import org.uw.nwgc.multiomics.util.coordinates_reader.parser.GeneNameValidator;
import org.uw.nwgc.multiomics.util.coordinates_reader.parser.IntervalParser;

import java.util.*;

/**
 * Assembles a {@link GenomicRecord} from a raw data row given a resolved column mapping.
 *
 * <p>Handles all column layouts:</p>
 * <ul>
 *   <li>Single interval column (e.g. {@code chr1:100-200})</li>
 *   <li>BED-style trio: separate chromosome, start, end columns</li>
 *   <li>Chromosome + position (point locus)</li>
 *   <li>Gene name / ID columns</li>
 *   <li>Strand column</li>
 * </ul>
 */
public class RecordAssembler {

    /**
     * Assemble a GenomicRecord from a raw row.
     *
     * @param rowIndex  0-based data row index (not counting header)
     * @param rawRow    ordered list of cell values
     * @param headers   ordered list of header names (may be null or empty)
     * @param groups    resolved column groups from {@link ColumnGroupResolver}
     * @return assembled record; coordinate will be null if unparseable
     */
    public GenomicRecord assemble(int rowIndex, List<String> rawRow, List<String> headers,
                                  List<ColumnGroupResolver.ColumnGroup> groups) {

        GenomicRecord.Builder builder = GenomicRecord.builder(rowIndex);

        // Store all raw values
        for (int i = 0; i < rawRow.size(); i++) {
            String h = (headers != null && i < headers.size()) ? headers.get(i) : "col_" + (i + 1);
            builder.rawValue(h, rawRow.get(i));
        }

        for (ColumnGroupResolver.ColumnGroup group : groups) {
            switch (group.groupType) {

                case INTERVAL_SINGLE, INTERVAL_PAIR -> {
                    if (!builder.hasCoordinate()) {
                        String val = getCellValue(rawRow, group.primary.getColumnIndex());
                        IntervalParser.parse(val).ifPresent(builder::coordinate);
                        if (!builder.hasCoordinate()) {
                            builder.warning(group.primary.getDisplayName(),
                                    "Could not parse interval value: " + val);
                        }
                    }
                }

                case BED_TRIO -> {
                    if (!builder.hasCoordinate()) {
                        builder.coordinate(assembleBedTrio(group, rawRow, builder));
                    }
                }

                case POSITION_ONLY -> {
                    if (!builder.hasCoordinate()) {
                        String val = getCellValue(rawRow, group.primary.getColumnIndex());
                        long pos = IntervalParser.parseLong(val);
                        if (pos > 0) {
                            builder.coordinate(GenomicCoordinate.builder()
                                    .start(pos)
                                    .sourceInterval(val)
                                    .coordinateSystem(GenomicCoordinate.CoordinateSystem.UNKNOWN)
                                    .build());
                        }
                    }
                }

                case GENE_ONLY -> {
                    String val = getCellValue(rawRow, group.primary.getColumnIndex());
                    if (val == null || val.isBlank()) break;

                    ColumnType type = group.primary.getColumnType();
                    var match = GeneNameValidator.classify(val);
                    match.ifPresent(m -> {
                        switch (m.type()) {
                            case HGNC_SYMBOL -> builder.geneName(m.normalized());
                            case ENSEMBL_GENE -> builder.geneId(m.normalized());
                            case ENSEMBL_TRANSCRIPT, REFSEQ_MRNA -> builder.transcriptId(m.normalized());
                            case RS_ID, CLINVAR_ID, HGVS -> builder.variantId(m.normalized());
                            default -> {
                                // Store in geneId as fallback
                                if (type == ColumnType.GENE_ID) builder.geneId(val);
                                else builder.geneName(val);
                            }
                        }
                    });
                    if (match.isEmpty() && !val.isBlank()) {
                        // Store anyway if header indicated gene column
                        if (type == ColumnType.GENE_NAME) builder.geneName(val);
                        else if (type == ColumnType.GENE_ID) builder.geneId(val);
                    }
                }
            }

            // Strand — scan supporting columns in all group types
            for (ColumnCandidate c : group.allColumns()) {
                if (c.getColumnType() == ColumnType.STRAND) {
                    String val = getCellValue(rawRow, c.getColumnIndex());
                    if (val != null && !val.isBlank()) builder.strand(val.trim());
                }
            }
        }

        return builder.build();
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private GenomicCoordinate assembleBedTrio(ColumnGroupResolver.ColumnGroup group,
                                               List<String> rawRow,
                                               GenomicRecord.Builder builder) {
        ColumnCandidate chrCol   = group.primary;
        ColumnCandidate startCol = null;
        ColumnCandidate endCol   = null;

        for (ColumnCandidate c : group.supporting) {
            if (c.getColumnType() == ColumnType.START_POSITION) startCol = c;
            if (c.getColumnType() == ColumnType.END_POSITION)   endCol   = c;
        }

        String chrVal   = getCellValue(rawRow, chrCol.getColumnIndex());
        String startVal = startCol != null ? getCellValue(rawRow, startCol.getColumnIndex()) : null;
        String endVal   = endCol   != null ? getCellValue(rawRow, endCol.getColumnIndex())   : null;

        Optional<String> chr = ChromosomeNormalizer.normalize(chrVal != null ? chrVal : "");
        if (chr.isEmpty()) {
            builder.warning(chrCol.getDisplayName(), "Could not normalize chromosome: " + chrVal);
            return null;
        }

        Long start = startVal != null ? safePos(startVal) : null;
        Long end   = endVal   != null ? safePos(endVal)   : null;

        if (start == null && startVal != null) {
            builder.warning(startCol != null ? startCol.getDisplayName() : "start",
                    "Could not parse start position: " + startVal);
        }
        if (end == null && endVal != null) {
            builder.warning(endCol != null ? endCol.getDisplayName() : "end",
                    "Could not parse end position: " + endVal);
        }

        String interval = start != null && end != null
                ? chrVal + ":" + start + "-" + end
                : chrVal + (start != null ? ":" + start : "");

        return GenomicCoordinate.builder()
                .rawChromosome(chrVal)
                .chromosome(chr.get())
                .start(start)
                .end(end)
                .sourceInterval(interval)
                .coordinateSystem(start != null && start == 0
                        ? GenomicCoordinate.CoordinateSystem.ZERO_BASED
                        : GenomicCoordinate.CoordinateSystem.ONE_BASED)
                .build();
    }

    private String getCellValue(List<String> row, int colIndex) {
        if (colIndex < 0 || colIndex >= row.size()) return null;
        String v = row.get(colIndex);
        return (v != null && !v.isBlank()) ? v.trim() : null;
    }

    private Long safePos(String val) {
        long v = IntervalParser.parseLong(val);
        return v >= 0 ? v : null;
    }
}
