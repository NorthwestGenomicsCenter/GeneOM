package org.uw.nwgc.multiomics.util.coordinates_reader;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.uw.nwgc.multiomics.util.coordinates_reader.detector.ColumnGroupResolver;
import org.uw.nwgc.multiomics.util.coordinates_reader.detector.ColumnGroupResolver.ColumnGroup;
import org.uw.nwgc.multiomics.util.coordinates_reader.detector.ColumnScorer;
import org.uw.nwgc.multiomics.util.coordinates_reader.detector.RecordAssembler;
import org.uw.nwgc.multiomics.util.coordinates_reader.io.SpreadsheetReader;
import org.uw.nwgc.multiomics.util.coordinates_reader.io.SpreadsheetReader.SheetData;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.ColumnCandidate;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.GenomicRecord;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main entry point for genomic file parsing.
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * try (var parser = GenomicFileParser.fromStream(inputStream, "variants.xlsx")) {
 *
 *     // 1. Detect columns — inspect what was found
 *     List<ColumnCandidate> candidates = parser.detectColumns();
 *     candidates.forEach(c -> System.out.println(c));
 *
 *     // 2. Stream records
 *     List<GenomicRecord> records = parser.parseRecords();
 *     records.stream()
 *             .filter(GenomicRecord::hasCoordinate)
 *             .forEach(r -> System.out.println(r.getCoordinate()));
 * }
 * }</pre>
 *
 * <h2>Auto-detection</h2>
 * <p>The parser sniffs the first ~30 data rows to score each column using header
 * keywords, value patterns, chromosome name recognition, and cross-column
 * coherence (e.g. a chromosome column adjacent to two numeric columns is likely
 * a BED trio). Results are returned with confidence scores.</p>
 *
 * <h2>Edge cases handled</h2>
 * <ul>
 *   <li>Multiple sheets in XLS/XLSX — first sheet by default, all accessible</li>
 *   <li>Combined interval columns (chr1:100-200) and split BED columns</li>
 *   <li>Paired assembly columns (GRCh37 + GRCh38 side by side)</li>
 *   <li>Files with no header row</li>
 *   <li>Thousand-comma formatted positions (1,234,567)</li>
 *   <li>Scientific notation from Excel (1.5E8 → 150000000)</li>
 *   <li>BOM in UTF-8/UTF-16 files</li>
 *   <li>Blank/sparse rows</li>
 * </ul>
 */
public class GenomicFileParser implements Closeable {

    /**
     * Result of column detection — includes both flat candidate list and
     * resolved structural groups.
     */
    public record DetectionResult(
            /** All scored column candidates, sorted by descending score. */
            List<ColumnCandidate> candidates,
            /**
             * Resolved column groups (BED trios, interval columns, gene columns, etc.),
             * sorted by descending group score.
             */
            List<ColumnGroup> groups,
            /** Column headers, if a header row was detected. May be empty. */
            List<String> headers,
            /** Sheet name this result came from. */
            String sheetName
    ) {
        /** Convenience: best single group for coordinate extraction. */
        public Optional<ColumnGroup> bestCoordinateGroup() {
            return groups.stream()
                    .filter(g -> g.groupType != ColumnGroup.GroupType.GENE_ONLY)
                    .findFirst();
        }

        /** All gene/ID candidates. */
        public List<ColumnCandidate> geneCandidates() {
            return candidates.stream()
                    .filter(c -> c.getColumnType() == ColumnCandidate.ColumnType.GENE_NAME
                              || c.getColumnType() == ColumnCandidate.ColumnType.GENE_ID
                              || c.getColumnType() == ColumnCandidate.ColumnType.TRANSCRIPT_ID
                              || c.getColumnType() == ColumnCandidate.ColumnType.VARIANT_ID)
                    .collect(Collectors.toList());
        }
    }

    // ── State ──────────────────────────────────────────────────────────────

    private byte[] sheetBytes;
    private final SpreadsheetReader reader;
    private final ColumnScorer scorer;
    private final ColumnGroupResolver resolver;
    private final RecordAssembler assembler;
    private final Config config;

    private SheetData sheetData;          // lazy loaded
    private DetectionResult detectionResult; // cached after first call

    // ── Configuration ──────────────────────────────────────────────────────

    public static class Config {
        /** Sheet index to use (0-based). Default 0. */
        public int sheetIndex = 0;

        /** Whether the file has a header row. Default true. */
        public boolean hasHeader = true;

        /**
         * If true, the parser will attempt to auto-detect whether a header row
         * is present by checking if the first row contains non-numeric, non-interval
         * values when all subsequent rows are numeric/coordinate. Default true.
         */
        public boolean autoDetectHeader = true;

        /** Minimum score for a column to be included as a candidate. Default 0.15. */
        public double minColumnScore = 0.15;

        /** Max rows to sample for column detection. Default 50. */
        public int sampleRows = 50;

        /** If true, skip rows where the coordinate is unparseable. Default false. */
        public boolean skipUnparseable = false;
    }

    // ── Constructors ───────────────────────────────────────────────────────

    private GenomicFileParser(SpreadsheetReader reader, Config config) {
        this.reader   = reader;
        this.config   = config;
        ColumnScorer.Config sc = new ColumnScorer.Config();
        sc.minScore   = config.minColumnScore;
        sc.sampleSize = config.sampleRows;
        this.scorer   = new ColumnScorer(sc);
        this.resolver = new ColumnGroupResolver();
        this.assembler = new RecordAssembler();
    }

    public static GenomicFileParser from(MultipartFile file) throws IOException {
        // MAX_FILE_SIZE 200MB for the file
        final long MAX_FILE_SIZE = 200 * 1024 * 1024;
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitExceededException(
                    "File exceeds maximum allowed size", file.getSize(), MAX_FILE_SIZE
            );
        }
        return GenomicFileParser.fromBytes(file.getBytes());
    }

    private static String detectTrueFormat(byte[] bytes, String declaredExtension) {
        if (isOoxmlFormat(bytes)) {
            // It's a real xlsx/zip-based file regardless of extension
            return "xlsx";
        }
        if (isPlainText(bytes)) {
            // Trust the extension for text-based formats (tsv, bed, vcf)
            return declaredExtension.toLowerCase().replace(".", "");
        }
        return declaredExtension.toLowerCase().replace(".", "");
    }

    private static boolean isOoxmlFormat(byte[] bytes) {
        // OOXML (xlsx) is a ZIP file — magic bytes are PK\x03\x04
        return bytes.length >= 4
                && bytes[0] == 0x50  // P
                && bytes[1] == 0x4B  // K
                && bytes[2] == 0x03
                && bytes[3] == 0x04;
    }

    private static boolean isPlainText(byte[] bytes) {
        // Sample the first 512 bytes — if no null bytes, treat as plain text
        int sampleSize = Math.min(bytes.length, 512);
        for (int i = 0; i < sampleSize; i++) {
            if (bytes[i] == 0x00) return false;
        }
        return true;
    }

    public static GenomicFileParser fromBytes(byte[] bytes) throws IOException {
        return new GenomicFileParser(SpreadsheetReader.fromBytes(bytes), new Config());
    }

    public static GenomicFileParser fromStream(InputStream is, Path filename) throws IOException {
        return new GenomicFileParser(SpreadsheetReader.fromStream(is, filename.getFileName().toString()), new Config());
    }

    public static GenomicFileParser fromStream(InputStream is, SpreadsheetReader.Format fmt) throws IOException {
        return new GenomicFileParser(SpreadsheetReader.fromStream(is, fmt), new Config());
    }

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Detect genomic columns and return candidates with confidence scores.
     *
     * <p>Returns a {@link DetectionResult} containing:</p>
     * <ul>
     *   <li>A flat list of all scored column candidates (sorted by score)</li>
     *   <li>Resolved column groups (BED trios, interval columns, etc.)</li>
     *   <li>Detected headers</li>
     * </ul>
     *
     * <p>Results are cached — repeated calls do not re-scan the file.</p>
     */
    public DetectionResult detectColumns() throws IOException {
        if (detectionResult != null) return detectionResult;

        SheetData data = loadSheet();
        boolean hasHeader = resolveHasHeader(data);

        List<String> headers = hasHeader && !data.rows().isEmpty()
                ? normalizeHeaders(data.rows().get(0))
                : List.of();

        List<List<String>> dataRows = hasHeader && !data.rows().isEmpty()
                ? data.rows().subList(1, data.rows().size())
                : data.rows();

        int colCount = dataRows.stream()
                .mapToInt(List::size)
                .max().orElse(0);

        // Score each column
        List<ColumnCandidate> candidates = new ArrayList<>();
        for (int col = 0; col < colCount; col++) {
            final int c = col;
            String header = (col < headers.size()) ? headers.get(col) : null;
            List<String> colValues = dataRows.stream()
                    .map(row -> c < row.size() ? row.get(c) : "")
                    .collect(Collectors.toList());

            scorer.score(col, header, colValues).ifPresent(candidates::add);
        }

        // Sort by descending score
        candidates.sort(Comparator.comparingDouble(ColumnCandidate::getScore).reversed());

        // Resolve cross-column groups
        List<ColumnGroup> groups = resolver.resolve(candidates);

        detectionResult = new DetectionResult(
                Collections.unmodifiableList(candidates),
                groups,
                headers,
                data.sheetName()
        );
        return detectionResult;
    }

    /**
     * Parse all data rows into {@link GenomicRecord}s using the auto-detected
     * column layout.
     *
     * <p>Rows where no genomic data could be extracted are included unless
     * {@link Config#skipUnparseable} is true.</p>
     *
     * @return list of records
     */
    public List<GenomicRecord> parseRecords() throws IOException {
        return parseRecords(detectColumns());
    }

    /**
     * Parse all data rows using an explicitly provided detection result.
     * Useful if you have reviewed/overridden the column assignments.
     */
    public List<GenomicRecord> parseRecords(DetectionResult detection) throws IOException {
        SheetData data = loadSheet();
        boolean hasHeader = resolveHasHeader(data);

        List<List<String>> dataRows = hasHeader && !data.rows().isEmpty()
                ? data.rows().subList(1, data.rows().size())
                : data.rows();

        List<ColumnGroup> groups = detection.groups();
        List<String> headers = detection.headers();

        List<GenomicRecord> records = new ArrayList<>(dataRows.size());
        for (int i = 0; i < dataRows.size(); i++) {
            List<String> rawRow = dataRows.get(i);

            // Skip completely empty rows
            if (rawRow.stream().allMatch(v -> v == null || v.isBlank())) continue;

            GenomicRecord record = assembler.assemble(i, rawRow, headers, groups);

            if (config.skipUnparseable && !record.hasAnyGenomicData()) continue;

            records.add(record);
        }
        return Collections.unmodifiableList(records);
    }

    /**
     * Parse records from a specific sheet (XLS/XLSX only).
     */
    public List<GenomicRecord> parseRecords(int sheetIndex) throws IOException {
        sheetData = null;          // reset cached sheet
        detectionResult = null;
        config.sheetIndex = sheetIndex;
        return parseRecords();
    }

    /** Returns all available sheet names (XLS/XLSX). For CSV/TSV returns ["CSV"] or ["TSV"]. */
    public List<String> sheetNames() throws IOException {
        return reader.sheetNames();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    // ── Private ────────────────────────────────────────────────────────────

    private SheetData loadSheet() throws IOException {
        if (sheetData == null) {
            sheetData = reader.readSheet(config.sheetIndex, config.hasHeader);
        }
        return sheetData;
    }

    private boolean resolveHasHeader(SheetData data) {
        if (!config.autoDetectHeader) return config.hasHeader;
        if (data.rows().size() < 2) return config.hasHeader;

        List<String> firstRow = data.rows().get(0);
        // If majority of first-row cells are non-numeric/non-parseable, treat as header
        long nonNumeric = firstRow.stream()
                .filter(v -> v != null && !v.isBlank())
                .filter(v -> !v.matches("[\\d,.E+\\-]+"))
                .count();
        long total = firstRow.stream().filter(v -> v != null && !v.isBlank()).count();
        if (total == 0) return config.hasHeader;

        return (double) nonNumeric / total >= 0.5;
    }

    private List<String> normalizeHeaders(List<String> rawHeaders) {
        return rawHeaders.stream()
                .map(h -> h == null ? "" : h.trim())
                .collect(Collectors.toList());
    }
}
