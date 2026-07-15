package org.uw.nwgc.multiomics.util.coordinates_reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uw.nwgc.multiomics.util.coordinates_reader.io.SpreadsheetReader;

import java.io.IOException;
import java.io.InputStream;

public class GenomicFileParserTests {

    @Test
    void parse() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/test_files/bedpe.annotation.xls")) {
            Assertions.assertNotNull(is, "Test resource file not found on classpath");

            try (GenomicFileParser parser = GenomicFileParser.fromStream(is, SpreadsheetReader.Format.TSV)) {
                var records = parser.parseRecords();
                Assertions.assertAll(
                        "TSV File with xls extension",
                        () -> Assertions.assertNotNull(records, "Test records not found"),
                        () -> Assertions.assertEquals(6, records.size(), "Correct number of records")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}