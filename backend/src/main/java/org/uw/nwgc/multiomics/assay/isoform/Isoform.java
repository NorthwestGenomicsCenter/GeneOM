package org.uw.nwgc.multiomics.assay.isoform;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "isoform")
public class Isoform {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "isoform_seq")
    @SequenceGenerator(name = "isoform_seq", sequenceName = "isoform_seq", allocationSize = 1)
    private Integer id;

    @JsonProperty("Isoform_PBid")
    private String isoformPBid;

    @JsonProperty("associated_gene")
    private String associatedGene;

    @JsonProperty("Sample")
    private String sample;

    @JsonProperty("Cyclo_TPM")
    private Double cycloTPM;

    @JsonProperty("Noncyclo_TPM")
    private Double noncycloTPM;

    @JsonProperty("test_statistic")
    private Double testStatistic;

    @JsonProperty("z_score")
    private Double zScore;

    @JsonProperty("NormalizedFractionDifference")
    private Double normalizedFractionDifference;

    @JsonProperty("OMIM_phenotypes")
    @Column(columnDefinition = "TEXT")
    private String omimPhenotypes;

    @JsonProperty("rank_top_99_9_percentile")
    private Integer rankTop999Percentile;

    @JsonProperty("rank_top_99_5_percentile")
    private Integer rankTop995Percentile;

    @JsonProperty("rank_top_99_percentile")
    private Integer rankTop99Percentile;

    @JsonProperty("rank_top_98_percentile")
    private Integer rankTop98Percentile;

    @JsonProperty("rank_top_95_percentile")
    private Integer rankTop95Percentile;

}
