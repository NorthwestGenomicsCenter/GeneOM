package org.uw.nwgc.multiomics.assay.tandem_repeats;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpression;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpressionChartDTO;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;

import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TandemRepeatsTransformer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("Tandem Repeats");
    }

    @Override
    public Object serveResource(Reader reader) {
        CsvMapper mapper = new CsvMapper();

        // Configure schema for TSV (tab-separated with header)
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader();

        DecimalFormat df = new DecimalFormat("#.###");
        Map<String, List<DifferentialExpression>> coordMap = new HashMap<>();
        List<DifferentialExpressionChartDTO> chartDtos = new ArrayList<>();

        try {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(DifferentialExpression.class)
                    .with(schema)
                    .readValues(reader);

            List<DifferentialExpression> data = iterator.readAll();
            for (DifferentialExpression dto : data) {
                String coords = df.format(dto.getLogFC()) + "," + df.format(dto.getLogFC());
                List<DifferentialExpression> collapsedPoint = coordMap.computeIfAbsent(coords, k -> new ArrayList<>());
                collapsedPoint.add(dto);
            }

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
        } catch (IOException e) {
        }

        return chartDtos;
    }
}
