package org.uw.nwgc.multiomics.assay.rnaseq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.util.Compute;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
public class RnaSeqController {

//    private final RnaSeqService rnaSeqService;
//    private static final String TEST_FILE_PATH = "/net/nwgc/vol1/techdev/multiomics-browser/diff_peak_expression.csv";
//
//    @Autowired
//    private RnaSeqController(RnaSeqService rnaSeqService) {
//        this.rnaSeqService = rnaSeqService;
//    };
//
//    @GetMapping(
//            value = "/assay/rnaseq/differentialPeak/{id}",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<List<DifferentialPeak>> getDifferentialPeakById(@PathVariable int id) throws IOException {
//        Path filePath = Path.of(TEST_FILE_PATH);
//        CsvMapper mapper = new CsvMapper();
//
//        // Configure schema for TSV (tab-separated with header)
//        CsvSchema schema = CsvSchema.emptySchema()
//                .withHeader();      // first line is header
//
//        try (InputStream inputStream = Files.newInputStream(filePath)) {
//            MappingIterator<DifferentialPeak> iterator = mapper
//                    .readerFor(DifferentialPeak.class)
//                    .with(schema)
//                    .readValues(inputStream);
//
//            return ResponseEntity.ok(iterator.readAll());
//        }
//    }
//
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public record DifferentialPeakMinMax(
//            @JsonProperty("fdr1") Compute.MinMax fdr1,
//            @JsonProperty("fdr2") Compute.MinMax fdr2,
//            @JsonProperty("log2fold1") Compute.MinMax log2fold1,
//            @JsonProperty("log2fold2") Compute.MinMax log2fold2
//        ) {}
//
//    @GetMapping(
//            value = "/assay/differentialPeak/filterLimits/{id}",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<DifferentialPeakMinMax> getDifferentialPeakFiltersById(@PathVariable int id) throws IOException {
//        Path filePath = Path.of(TEST_FILE_PATH);
//        CsvMapper mapper = new CsvMapper();
//
//        // Configure schema for TSV (tab-separated with header)
//        CsvSchema schema = CsvSchema.emptySchema()
//                .withHeader();      // first line is header
//
//        try (InputStream inputStream = Files.newInputStream(filePath)) {
//            MappingIterator<DifferentialPeak> iterator = mapper
//                    .readerFor(DifferentialPeak.class)
//                    .with(schema)
//                    .readValues(inputStream);
//
//            List<DifferentialPeak> contents = iterator.readAll();
//            DifferentialPeakMinMax differentialPeakMinMax = new DifferentialPeakMinMax(
//                    Compute.minMax(contents, DifferentialPeak::getFdr1),
//                    Compute.minMax(contents, DifferentialPeak::getFdr2),
//                    Compute.minMax(contents, DifferentialPeak::getLogFold1),
//                    Compute.minMax(contents, DifferentialPeak::getLogFold2)
//            );
//
//            return ResponseEntity.ok(differentialPeakMinMax);
//        }
//    }

}
