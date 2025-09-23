package org.uw.nwgc.multiomics.assay;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.http.HttpHeaders;
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
public class DiffExpressionScatterVisualizer implements VizTransformer {

    public record DataPoint(String chr, Integer pos, Double y) {};

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

    @Override
    public boolean validateResource(Reader reader) {
        return false;
    }

    @JsonProperty("unknown")
    private String unknown;

    @JsonProperty("gene_id")
    private String geneId;

    @JsonProperty("gene_name")
    private String geneName;

    @JsonProperty("logFC")
    private Double logFC;

    @JsonProperty("logCPM")
    private Double logCPM;

    @JsonProperty("F")
    private String F;

    @JsonProperty("PValue")
    private String pValue;

    @JsonProperty("FDR")
    private Double fdr;

    @JsonProperty("seqnames")
    private String seqnames;

    @JsonProperty("start")
    private String start;

    @JsonProperty("end")
    private String end;

    @JsonProperty("width")
    private String width;

    @JsonProperty("strand")
    private String strand;

    @JsonProperty("source")
    private String source;

    @JsonProperty("type")
    private String type;

    @JsonProperty("score")
    private String score;

    @JsonProperty("phase")
    private String phase;

    @JsonProperty("transcript_id")
    private String transcriptId;

    @JsonProperty("gene_type")
    private String geneType;

    @JsonProperty("transcript_type")
    private String transcriptType;

    @JsonProperty("transcript_name")
    private String transcriptName;

    @JsonProperty("level")
    private String level;

    @JsonProperty("hgnc_id")
    private String hgncId;

    @JsonProperty("havana_gene")
    private String havanaGene;

    @JsonProperty("exon_id")
    private String exonId;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("gene_status")
    private String geneStatus;

    @JsonProperty("transcript_status")
    private String transcriptStatus;
}
