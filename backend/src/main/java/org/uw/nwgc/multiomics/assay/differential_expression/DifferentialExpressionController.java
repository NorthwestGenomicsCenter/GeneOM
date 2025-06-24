package org.uw.nwgc.multiomics.assay.differential_expression;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DifferentialExpressionController {

    private final DifferentialExpressionService differentialExpressionService;
    private static final String TEST_FILE_PATH = "/net/nwgc/vol1/techdev/multiomics-browser/carbone_diff_expression.csv";

    @Autowired
    private DifferentialExpressionController(DifferentialExpressionService differentialExpressionService) {
        this.differentialExpressionService = differentialExpressionService;
    };

    @GetMapping(
            value = "/assay/differential_expression/chart/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<DifferentialExpressionChartDTO>> getDifferentialExpressionChartById(@PathVariable int id) throws IOException {
        Path filePath = Path.of(TEST_FILE_PATH);
        CsvMapper mapper = new CsvMapper();

        // Configure schema for TSV (tab-separated with header)
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader();

        DecimalFormat df = new DecimalFormat("#.###");
        Map<String, List<DifferentialExpression>> coordMap = new HashMap<>();

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(DifferentialExpression.class)
                    .with(schema)
                    .readValues(inputStream);

            List<DifferentialExpression> data = iterator.readAll();
            for (DifferentialExpression dto : data) {
                String coords = df.format(dto.getLogFC()) + "," + df.format(dto.getLogFC());
                List<DifferentialExpression> collapsedPoint = coordMap.computeIfAbsent(coords, k -> new ArrayList<>());
                collapsedPoint.add(dto);
            }

            List<DifferentialExpressionChartDTO> chartDtos = new ArrayList<>();
            for (List<DifferentialExpression> position : coordMap.values()) {
                DifferentialExpressionChartDTO dto = new DifferentialExpressionChartDTO();
                dto.setLogFC(position.getFirst().getLogFC());
                dto.setLogCPM(position.getFirst().getLogCPM());
                List<String> names = dto.getGeneNames();
                for (DifferentialExpression gene : position) {
                    names.add(gene.getGeneName());
                }
                chartDtos.add(dto);
            }

            System.out.println("coordMap.size() = " + coordMap.size());
            System.out.println("data.size() = " + data.size());

            return ResponseEntity.ok(chartDtos);
        }
    }

    @GetMapping(
            value = "/assay/differential_expression/table/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<DifferentialExpression>> getDifferentialExpressionId(@PathVariable int id) throws IOException {
        ResponseEntity<DifferentialMinMax> value = getDifferentialExpressionIdWithFilters(1);
        DifferentialMinMax body = value.getBody();
        System.out.println("body = " + body);

        Path filePath = Path.of(TEST_FILE_PATH);
        CsvMapper mapper = new CsvMapper();

        // Configure schema for TSV (tab-separated with header)
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader();

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(DifferentialExpression.class)
                    .with(schema)
                    .readValues(inputStream);

            return ResponseEntity.ok(iterator.readAll());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DifferentialMinMax(
            @JsonProperty("logFc") Compute.MinMax logFc,
            @JsonProperty("logCpm") Compute.MinMax logCpm
    ) {}

    @GetMapping(
            value = "/assay/differential_expression/minmax/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DifferentialMinMax> getDifferentialExpressionIdWithFilters(@PathVariable int id) throws IOException {
        Path filePath = Path.of(TEST_FILE_PATH);
        CsvMapper mapper = new CsvMapper();

        // Configure schema for TSV (tab-separated with header)
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader();

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(DifferentialExpression.class)
                    .with(schema)
                    .readValues(inputStream);

            List<DifferentialExpression> data = iterator.readAll();
            return ResponseEntity.ok(new DifferentialMinMax(
                Compute.minMax(data, DifferentialExpression::getLogFC),
                Compute.minMax(data, DifferentialExpression::getLogCPM)
            ));
        }
    }


}
