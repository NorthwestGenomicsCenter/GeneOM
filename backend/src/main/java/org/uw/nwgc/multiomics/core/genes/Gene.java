package org.uw.nwgc.multiomics.core.genes;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Gene {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gene_seq")
    @SequenceGenerator(name = "gene_seq", sequenceName = "gene_seq", allocationSize = 1)
    private Integer id;
    public String name;
    public String ensemblGeneId;
    public String symbol;

    @ElementCollection
    @CollectionTable(name="gene_groups", joinColumns=@JoinColumn(name="gene_id"))
    @Column(name="gene_group")
    public List<String> geneGroup;

    public Gene(GeneImport.Doc doc) {
        this.name = doc.name;
        this.ensemblGeneId = doc.ensembl_gene_id;
        this.symbol = doc.symbol;
        this.geneGroup = doc.gene_group;
    }

}
