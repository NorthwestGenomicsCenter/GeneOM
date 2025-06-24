package org.uw.nwgc.multiomics.assay.differential_expression;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DifferentialExpressionChartDTO {

    @JsonProperty("geneId")
    private String geneId;

    @JsonProperty("geneNames")
    private List<String> geneNames = new ArrayList<>();

    @JsonProperty("logFC")
    private Double logFC;

    @JsonProperty("logCPM")
    private Double logCPM;

    public DifferentialExpressionChartDTO(DifferentialExpression differentialExpression) {
        this.geneId = differentialExpression.getGeneId();
//        this.geneName = differentialExpression.getGeneName();
        this.logFC = differentialExpression.getLogFC();
        this.logCPM = differentialExpression.getLogCPM();
    }
}
