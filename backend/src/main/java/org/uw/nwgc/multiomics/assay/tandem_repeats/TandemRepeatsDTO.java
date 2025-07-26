package org.uw.nwgc.multiomics.assay.tandem_repeats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class TandemRepeatsDTO {

    @JsonProperty("priority_rank")
    private String priority_rank; // Rank according to priority flags

    @JsonProperty("flags")
    private String flags; // Flags indicating this locus might be high priority

    @JsonProperty("filters")
    private String filters; // Filters indicating columns that should be examined for potential quality issues

    @JsonProperty("chrom")
    private String chrom; // Chromosome

    @JsonProperty("start")
    private String start; // Start position of locus

    @JsonProperty("end")
    private String end; // End position of locus

    @JsonProperty("variant_id")
    private String variant_id; // TRGT ID

    @JsonProperty("STRUC")
    private String STRUC; // Structure of the repeat

    @JsonProperty("MOTIFS")
    private String MOTIFS; // Motifs in tandem repeat

    @JsonProperty("PS")
    private String PS; // Phase set

    @JsonProperty("SD")
    private String SD; // Number of reads spanning each allele

    @JsonProperty("MC")
    private String MC; // Count of motifs of each TR on each allele

    @JsonProperty("pathogenic_motifs")
    private String pathogenic_motifs; // Motifs known to be associated with pathogenicity

    @JsonProperty("pathogenic_MC")
    private String pathogenic_MC; // Summed counts of all pathogenic motifs on each allele

    @JsonProperty("pathogenic")
    private String pathogenic; // Genotypes containing pathogenic or premutation alleles

    @JsonProperty("normal_min")
    private String normal_min; // Normal minimum for pathogenic motif counts

    @JsonProperty("normal_max")
    private String normal_max; // Normal maximum for pathogenic motif counts

    @JsonProperty("pathogenic_expansion_min")
    private String pathogenic_expansion_min; // Pathogenic threshold for expansions

    @JsonProperty("pathogenic_contraction_max")
    private String pathogenic_contraction_max; // Pathogenic threshold for contractions

    @JsonProperty("AL")
    private String AL; // Allele length in bps

    @JsonProperty("ALLR")
    private String ALLR; // Allele length range in bps

    @JsonProperty("expanded")
    private String expanded; // Genotypes containing expanded alleles

    @JsonProperty("pop_upper")
    private String pop_upper; // Population upper cutoff

    @JsonProperty("pop_recessive_upper")
    private String pop_recessive_upper; // Population upper cutoff calculated from only shortest allele in each genotype

    @JsonProperty("gene")
    private String gene; // Gene symbols for consequences listed in consequence

    @JsonProperty("highest_consequence")
    private String highest_consequence; // Highest consequence from consequence column

    @JsonProperty("highest_impact")
    private String highest_impact; // Highest impact from consequence column

    @JsonProperty("consequence")
    private String consequence; // Consequence annotation from VEP in format SYMBOL|Gene|Feature|Feature_type|Consequence|BIOTYPE|IMPACT

    @JsonProperty("disease_gene")
    private String disease_gene; // Gene name from annotation TSV

    @JsonProperty("disease_id")
    private String disease_id; // Disease ID from annotation TSV

    @JsonProperty("disease_name")
    private String disease_name; // Disease name from annotation TSV

    @JsonProperty("inheritance")
    private String inheritance; // Mode of inheritance for disease

    @JsonProperty("notes")
    private String notes; // Notes from annotation TSV
}
