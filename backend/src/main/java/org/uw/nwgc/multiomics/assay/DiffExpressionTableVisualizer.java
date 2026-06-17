package org.uw.nwgc.multiomics.assay;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpression;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpressionChartDTO;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiffExpressionTableVisualizer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("Differential Expression");
    }

    @Override
    public Object serveResource(Reader reader, HttpHeaders header) {
        CsvMapper mapper = new CsvMapper();

        // Configure schema for TSV (tab-separated with header)
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader();

        try {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(DifferentialExpression.class)
                    .with(schema)
                    .readValues(reader);

            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validateResource(Reader reader, String pathString) {
        return false;
    }

}
