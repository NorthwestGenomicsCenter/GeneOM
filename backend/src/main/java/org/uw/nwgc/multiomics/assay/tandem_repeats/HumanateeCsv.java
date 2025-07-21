package org.uw.nwgc.multiomics.assay.tandem_repeats;

public class HumanateeCsv {
    private String priority_rank; //	Rank according to priority flags	default
    private String flags; //	Flags indicating this locus might be high priority	default
    private String filters; //	Filters indicating columns that should be examined for potential quality issues	default
    private String chrom; //	Chromosome	default
    private String start; //	Start position of locus	default
    private String end; //	End position of locus	default
    private String variant_id; //	TRGT ID	default
    private String STRUC; //	Structure of the repeat	default
    private String MOTIFS; //	Motifs in tandem repeat	default
    private String PS; //	Phase set	default
    private String SD; //	Number of reads spanning each allele	default
    private String MC; //	Count of motifs of each TR on each allele	default
    private String mother_MC; //	Summed motif counts for maternal alleles	denovo
    private String father_MC; //	Summed motif counts for paternal alleles	denovo
    private String pathogenic_motifs; //	Motifs known to be associated with pathogenicity	pathogenic-tsv
    private String pathogenic_MC; //	Summed counts of all pathogenic motifs on each allele	pathogenic-tsv
    private String pathogenic; //	Genotypes containing pathogenic or premutation alleles, determined by comparing pathogenic motif counts to the normal range(s) and pathogenic threshold(s)	pathogenic-tsv
    private String normal_min; //	Normal minimum for pathogenic motif counts	pathogenic-tsv
    private String normal_max; //	Normal maximum for pathogenic motif counts	pathogenic-tsv
    private String pathogenic_expansion_min; //	Pathogenic threshold for expansions	pathogenic-tsv
    private String pathogenic_contraction_max; //	Pathogenic threshold for contractions	pathogenic-tsv
    private String pathogenic_threshold; //	Pathogenic threshold(s) of pathogenic motif counts	pathogenic-tsv
    private String AL; //	Allele length in bps	default
    private String ALLR; //	Allele length range in bps	default
    private String mother_AL; //	Mother allele lengths	denovo
    private String father_AL; //	Father allele lengths	denovo
    private String expanded; //	Genotypes containing expanded alleles, determined by comparing summed motif counts (all motifs) to the population cutoff, the shortest allele in each genotype is compared to the recessive cutoff	population-db
    private String pop_upper; //	Population upper cutoff	population-db
    private String pop_cutoff_n; //	Number of population alleles used to calculate population cutoffs	population-db
    private String pop_recessive_upper; //	Population upper cutoff calculated from only shortest allele in each genotype of population	population-db
    private String pop_recessive_cutoff_n; //	Number of population alleles used to calculate population cutoffs	population-db
    private String coverage_dropout; //	Full dropout (<2x coverage) or haplotype dropout (<2x coverage per haplotype	dropouts
    private String mother_dropout; //	Coverage cut-off dropout detection using HP tags from phasing tools in mother; possible values: Full dropout (FD), Haplotype dropout (HD), Not (N)	denovo
    private String father_dropout; //	Coverage cut-off dropout detection using HP tags from phasing tools in father; possible values: Full dropout (FD), Haplotype dropout (HD), Not (N)	denovo
    private String denovo_status; //	Indicates if the allele is potentially de novo; possible values: {X, Y:{+, -, =}}. This is X if no de novo read is found and Y otherwise, if parental origin can be determined without ambiguity the allele sequences can be compared directly such that the de novo type can be established as + (expansion), - (contraction), or = (substitution)	denovo
    private String allele_ratio; //	Ratio of de novo coverage to allele coverage	denovo
    private String child_ratio; //	Ratio of de novo coverage to total coverage at this site	denovo
    private String allele_coverage; //	Number of child reads mapped to this specific allele	denovo
    private String gene; //	Gene symbols for consequences listed in consequence, empty if there is no ALT allele	INFO/CSQ
    private String highest_consequence; //	Highest consequence from consequence column	INFO/CSQ
    private String highest_impact; //	Highest impact from consequence column, order: HIGH, MODERATE, LOW, MODIFIER	INFO/CSQ
    private String consequence; //	Consequence annotation from VEP in format SYMBOL|Gene|Feature|Feature_type|Consequence|BIOTYPE|IMPACT, empty if there is no ALT allele	INFO/CSQ
    private String disease_gene; //	Gene name from annotation TSV	pathogenic-tsv
    private String disease_id; //	Disease ID from annotation TSV	pathogenic-tsv
    private String disease_name; //	Disease name from annotation TSV	pathogenic-tsv
    private String inheritance; //	Mode of inheritance for disease	pathogenic-tsv
    private String notes; //	Notes from annotation TSV
}
