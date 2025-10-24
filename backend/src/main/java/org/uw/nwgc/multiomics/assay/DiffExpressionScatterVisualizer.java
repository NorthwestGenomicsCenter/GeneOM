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
import org.uw.nwgc.multiomics.util.RgbColor;

import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiffExpressionScatterVisualizer implements VizTransformer {

    public record DataPoint(
            String chr,
            Integer pos,
            Double y,
            Double size,
            RgbColor rgb) {};

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
        List<DataPoint> dataPoints = new ArrayList<>();

        try {
            MappingIterator<DifferentialExpression> iterator = mapper
                    .readerFor(DifferentialExpression.class)
                    .with(schema)
                    .readValues(reader);

            List<DifferentialExpression> data = iterator.readAll();
            for (DifferentialExpression row : data) {
                if ("NA".equals(row.getSeqnames()) ||
                        "NA".equals(row.getEnd()) ||
                        "NA".equals(row.getStart())) continue;
                String chr = row.getSeqnames();
                Integer pos = Math.abs(Integer.parseInt(row.getEnd()) - Integer.parseInt(row.getStart())) + Integer.parseInt(row.getStart());
                Double y = -1.0 * Math.log10(row.getFdr());

                Double size = Math.random() * 5;
                RgbColor color = new RgbColor(0, (int) (Math.random() * 256), (int) (Math.random() * 256));

                dataPoints.add(new DataPoint(chr, pos, y, size, color));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataPoints;
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
