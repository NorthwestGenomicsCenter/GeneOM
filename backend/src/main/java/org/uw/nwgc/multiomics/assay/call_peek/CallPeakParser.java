package org.uw.nwgc.multiomics.assay.call_peek;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CallPeakParser {

    public enum FileType {
        XLS, XLSX, TSV, CSV
    }

    public static List<CallPeak> parse(Path path) throws IOException {
        return parse(Files.readAllBytes(path));
    }

    public static List<CallPeak> parse(byte[] bytes) throws IOException {
        try {
            return parseExcel(new XSSFWorkbook(new ByteArrayInputStream(bytes)));
        } catch (Exception e) {
            return parseTsv(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes))));
        }
    }

    private static List<CallPeak> parseTsv(BufferedReader reader) throws IOException {
        List<CallPeak> peaks = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.isBlank()) continue;
            if (line.startsWith("chr\t") || line.startsWith("chr,")) continue;
            String[] cols = line.split("\t|,");
            if (cols.length < 10) continue;
            peaks.add(fromColumns(cols));
        }
        return peaks;
    }

    private static List<CallPeak> parseExcel(Workbook workbook) {
        List<CallPeak> peaks = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            if (firstCell == null) continue;
            String firstVal = firstCell.toString().trim();
            if (firstVal.isBlank() || firstVal.startsWith("#") || firstVal.equalsIgnoreCase("chr")) continue;
            String[] cols = new String[10];
            for (int i = 0; i < 10; i++) {
                Cell cell = row.getCell(i);
                cols[i] = cell != null ? cell.toString().trim() : "";
            }
            peaks.add(fromColumns(cols));
        }
        return peaks;
    }

    private static CallPeak fromColumns(String[] cols) {
        CallPeak peak = new CallPeak();
        peak.setChr(cols[0].trim());
        peak.setStart(Long.parseLong(cols[1].trim()));
        peak.setEnd(Long.parseLong(cols[2].trim()));
        peak.setLength(Integer.parseInt(cols[3].trim()));
        peak.setAbsSummit(Long.parseLong(cols[4].trim()));
        peak.setPileup(Double.parseDouble(cols[5].trim()));
        peak.setLog10Pvalue(Double.parseDouble(cols[6].trim()));
        peak.setFoldEnrichment(Double.parseDouble(cols[7].trim()));
        peak.setLog10Qvalue(Double.parseDouble(cols[8].trim()));
        peak.setName(cols[9].trim());
        return peak;
    }

    private static InputStream readerToStream(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int ch;
        while ((ch = br.read()) != -1) baos.write(ch);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}