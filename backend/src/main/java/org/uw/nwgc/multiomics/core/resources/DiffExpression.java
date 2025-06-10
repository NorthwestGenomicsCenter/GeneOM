package org.uw.nwgc.multiomics.core.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiffExpression implements VizTransformer{

    @Override
    public List<String> visualizationComponents() {
        return List.of("Differential Expression");
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
