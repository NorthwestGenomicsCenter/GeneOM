package org.uw.nwgc.multiomics.assay;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpression;
import org.uw.nwgc.multiomics.core.transformer.ServableResource;
import org.uw.nwgc.multiomics.core.transformer.VizTransformer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

@Component
public class DiffExpressionTableVisualizer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("Differential Expression");
    }

    @Override
    public Object serveResource(ServableResource servableResource) throws IOException {
        Reader reader = servableResource.getReader();
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
