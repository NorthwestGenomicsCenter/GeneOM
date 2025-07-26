package org.uw.nwgc.multiomics.assay.tandem_repeats;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpression;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;

import java.io.Reader;
import java.util.List;

@Component
public class TandemRepeatsTransformer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("Tandem Repeats");
    }

    @Override
    public Object serveResource(Reader reader, HttpHeaders header) {
        CsvMapper mapper = new CsvMapper();

        // Configure schema for TSV (tab-separated with header)
        CsvSchema schema = CsvSchema
                .emptySchema()
                .withColumnSeparator('\t')
                .withHeader();

        CsvSchema csvSchema = mapper.schemaFor(TandemRepeatsDTO.class)
                .withHeader()
                .withColumnSeparator('\t')
                .withColumnReordering(true);

        try {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(TandemRepeatsDTO.class)
                    .with(csvSchema)
                    .readValues(reader);

            return iterator.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean validateResource(Reader reader) {
        return serveResource(reader, null) != null;
    }
}
