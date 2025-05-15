package org.uw.nwgc.multiomics.assay.differential_expression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
// @Entity
// @Table(name = "isoform")
public class DifferentialPeak {

    @JsonProperty("peakID")
    private String peakId;

    @JsonProperty("chrom")
    private String chrom;

    @JsonProperty("start")
    private String start;

    @JsonProperty("stop")
    private String stop;

    @JsonProperty("fdr1")
    private Double fdr1;

    @JsonProperty("fdr2")
    private Double fdr2;

    @JsonProperty("log2fold1")
    private Double logFold1;

    @JsonProperty("log2fold2")
    private Double logFold2;

    @JsonProperty("overlappingGene")
    private String overLappingGenes;

    @JsonProperty("finalCode")
    private String finalCode;

    @JsonProperty("fullCode")
    private String fullCode;

    @JsonProperty("ofInterest")
    private String selected = "Unselected";
}
