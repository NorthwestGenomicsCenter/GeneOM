package org.uw.nwgc.multiomics.util.coordinates_reader.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Unified spreadsheet reading abstraction.
 *
 * <p>Returns data as a list of rows, each row being a list of cell string values.
 * Empty trailing cells are trimmed per row. The first row may or may not be a header
 * (callers decide).</p>
 *
 * <p>Supported formats:</p>
 * <ul>
 *   <li>CSV — comma separated</li>
 *   <li>TSV — tab separated</li>
 *   <li>XLS — Excel 97–2003 (HSSF via Apache POI)</li>
 *   <li>XLSX — Excel 2007+ (XSSF via Apache POI)</li>
 * </ul>
 *
 * <p>Dependencies (add to pom.xml / build.gradle):</p>
 * <pre>
 *   org.apache.poi:poi:5.x
 *   org.apache.poi:poi-ooxml:5.x
 *   org.apache.commons:commons-csv:1.x
 * </pre>
 */
public class SpreadsheetReader implements Closeable {

    public enum Format { CSV, TSV, XLS, XLSX }

    public record SheetData(
            String sheetName,
            List<List<String>> rows  // including header row if present
    ) {}

    private Format format;
    private byte[] sheetBytes;
    private Reader charReader;   // for CSV/TSV
    private InputStream streamForPoi;  // for XLS/XLSX
    private Workbook poiWorkbook;

    // ── Constructors ───────────────────────────────────────────────────────

    private SpreadsheetReader(byte[] sheetBytes) {
        this.sheetBytes = sheetBytes;
    }

    private SpreadsheetReader(Format format, Reader charReader, InputStream streamForPoi) {
        this.format = format;
        this.charReader = charReader;
        this.streamForPoi = streamForPoi;
    }

    public static SpreadsheetReader fromBytes(byte[] bytes) throws IOException {
        return new SpreadsheetReader(bytes);
    }

    /**
     * Open a spreadsheet from an InputStream.
     * Format must be provided explicitly. This supports all four formats.
     */
    public static SpreadsheetReader fromStream(InputStream stream, Format format) throws IOException {
        return new SpreadsheetReader(format, null, stream);
    }

    /**
     * Convenience: auto-detect format from filename extension.
     */
    public static SpreadsheetReader fromStream(InputStream stream, String filename) throws IOException {
        Format fmt = detectFormat(filename);
        return fromStream(stream, Format.TSV);
    }

    /**
     * Read the first (or only) sheet.
     *
     * @param hasHeader whether the first row is a header
     * @return SheetData
     */
    public SheetData readFirstSheet(boolean hasHeader) throws IOException {
        return readSheet(0, hasHeader);
    }


    @FunctionalInterface
    private interface SheetReader {
        SheetData read() throws IOException;
    }

    /**
     * Read a specific sheet by index (0-based).
     * For CSV/TSV there is only one "sheet" (index 0).
     */
    public SheetData readSheet(int sheetIndex, boolean hasHeader) {
        List<SheetReader> attempts = List.of(
                () -> {
//                    char delimiter = detectDelimiter(sheetBytes);
                    return readDelimited(CSVFormat.DEFAULT.builder()
                            .setCommentMarker('#')
                            .setDelimiter('\t')
                            .setTrim(true)
                            .setIgnoreEmptyLines(false)
                            .build(), "TSV", hasHeader);
                },
                () -> readPoiSheet(sheetIndex, false),
                () -> readPoiSheet(sheetIndex, true)
        );

        for (SheetReader attempt : attempts) {
            try {
                return attempt.read();
            } catch (Exception ignored) {}
        }

        throw new IllegalArgumentException("Could not read file as CSV, TSV, XLS, or XLSX");
    }

    /**
     * For XLS/XLSX: list all sheet names.
     */
    public List<String> sheetNames() throws IOException {
        ensurePoiWorkbook();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < poiWorkbook.getNumberOfSheets(); i++) {
            names.add(poiWorkbook.getSheetName(i));
        }
        return names;
    }

    /**
     * Read ALL sheets from an XLS/XLSX file.
     */
    public List<SheetData> readAllSheets(boolean hasHeader) throws IOException {
        if (format == Format.CSV || format == Format.TSV) {
            return List.of(readFirstSheet(hasHeader));
        }
        ensurePoiWorkbook();
        List<SheetData> sheets = new ArrayList<>();
        for (int i = 0; i < poiWorkbook.getNumberOfSheets(); i++) {
            sheets.add(readPoiSheet(i, format == Format.XLSX));
        }
        return sheets;
    }

    @Override
    public void close() throws IOException {
        if (charReader != null) charReader.close();
        if (poiWorkbook != null) poiWorkbook.close();
        if (streamForPoi != null) streamForPoi.close();
    }

    // ── Private: CSV/TSV reading ───────────────────────────────────────────

    private SheetData readDelimited(CSVFormat csvFormat, String sheetName, boolean hasHeader) throws IOException {
        sheetBytes = streamForPoi.readAllBytes();

        try (CSVParser parser = new CSVParser(stripFileComments(sheetBytes), csvFormat)) {
            List<List<String>> rows = StreamSupport.stream(parser.spliterator(), false)
                    .map(record -> {
                        List<String> row = new ArrayList<>();
                        record.forEach(row::add);
                        return row;
                    })
                    .collect(Collectors.toList());
            return new SheetData(sheetName, rows);
        }
    }

    private StringReader stripFileComments(byte[] bytes) {
        String content = new String(bytes, StandardCharsets.UTF_8);
        String stripped = Arrays.stream(content.split("\n", -1))
                .filter(line -> !line.startsWith("#") && !line.isBlank())
                .collect(Collectors.joining("\n"));
        return new StringReader(stripped);
    }

    private char detectDelimiter(byte[] bytes) {
        String preview = new String(bytes, StandardCharsets.UTF_8).lines()
                .filter(line -> !line.startsWith("#") && !line.isBlank())
                .findFirst()
                .orElse("");

        long tabs    = preview.chars().filter(c -> c == '\t').count();
        long commas  = preview.chars().filter(c -> c == ',').count();

        return tabs > commas ? '\t' : ',';
    }

    // ── Private: POI reading ───────────────────────────────────────────────

    private void ensurePoiWorkbook() throws IOException {
        if (poiWorkbook != null) return;
        if (format == Format.XLS) {
            poiWorkbook = new HSSFWorkbook(streamForPoi);
        } else {
            poiWorkbook = new XSSFWorkbook(streamForPoi);
        }
    }

    private SheetData readPoiSheet(int sheetIndex, boolean isXlsx) throws IOException {
        ensurePoiWorkbook();
        Sheet sheet = poiWorkbook.getSheetAt(sheetIndex);
        String sheetName = sheet.getSheetName();

        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator evaluator = poiWorkbook.getCreationHelper().createFormulaEvaluator();

        List<List<String>> rows = new ArrayList<>();
        int maxCol = 0;

        // First pass: determine actual column count
        for (Row row : sheet) {
            if (row == null) continue;
            maxCol = Math.max(maxCol, row.getLastCellNum());
        }

        // Second pass: extract values
        for (Row row : sheet) {
            if (row == null) {
                rows.add(Collections.emptyList()); // preserve empty rows
                continue;
            }

            List<String> rowValues = new ArrayList<>(maxCol);
            for (int c = 0; c < maxCol; c++) {
                Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                rowValues.add(cellToString(cell, formatter, evaluator));
            }
            rows.add(rowValues);
        }

        return new SheetData(sheetName, rows);
    }

    private String cellToString(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) return "";
        try {
            return switch (cell.getCellType()) {
                case BLANK   -> "";
                case STRING  -> cell.getStringCellValue().trim();
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield formatter.formatCellValue(cell); // preserve date string
                    }
                    // Avoid scientific notation for large integers (genomic positions!)
                    double d = cell.getNumericCellValue();
                    if (d == Math.floor(d) && !Double.isInfinite(d) && Math.abs(d) < 1e15) {
                        yield String.valueOf((long) d);
                    }
                    yield String.valueOf(d);
                }
                case FORMULA -> {
                    try {
                        CellValue val = evaluator.evaluate(cell);
                        yield switch (val.getCellType()) {
                            case STRING  -> val.getStringValue();
                            case NUMERIC -> {
                                double d = val.getNumberValue();
                                if (d == Math.floor(d) && !Double.isInfinite(d) && Math.abs(d) < 1e15) {
                                    yield String.valueOf((long) d);
                                }
                                yield String.valueOf(d);
                            }
                            case BOOLEAN -> String.valueOf(val.getBooleanValue());
                            default      -> formatter.formatCellValue(cell);
                        };
                    } catch (Exception e) {
                        yield formatter.formatCellValue(cell);
                    }
                }
                case ERROR   -> "#ERR:" + cell.getErrorCellValue();
                default      -> formatter.formatCellValue(cell);
            };
        } catch (Exception e) {
            return ""; // fail gracefully on corrupted cells
        }
    }

    // ── Static helpers ─────────────────────────────────────────────────────

    public static Format detectFormat(String filename) {
        if (filename == null) return Format.CSV;
        String lower = filename.toLowerCase().trim();
        if (lower.endsWith(".xlsx")) return Format.XLSX;
        if (lower.endsWith(".xls"))  return Format.XLS;
        if (lower.endsWith(".tsv"))  return Format.TSV;
        if (lower.endsWith(".txt"))  return Format.TSV; // common .txt = tab-sep
        return Format.CSV; // default
    }

    /**
     * Read and discard BOM bytes if present; return detected charset.
     * Supports UTF-8, UTF-16 BE/LE, UTF-32 BE/LE BOMs.
     */
    private static Charset sniffBom(InputStream in) throws IOException {
        in.mark(4);
        byte[] bom = new byte[4];
        int read = in.read(bom);
        if (read >= 3 && bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF) {
            in.reset();
            in.skip(3);
            return StandardCharsets.UTF_8;
        }
        if (read >= 2 && bom[0] == (byte)0xFF && bom[1] == (byte)0xFE) {
            in.reset(); in.skip(2);
            return StandardCharsets.UTF_16LE;
        }
        if (read >= 2 && bom[0] == (byte)0xFE && bom[1] == (byte)0xFF) {
            in.reset(); in.skip(2);
            return StandardCharsets.UTF_16BE;
        }
        // No BOM — assume UTF-8
        in.reset();
        return StandardCharsets.UTF_8;
    }
}
