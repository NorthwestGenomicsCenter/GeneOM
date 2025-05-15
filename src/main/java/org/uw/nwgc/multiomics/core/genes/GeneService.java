package org.uw.nwgc.multiomics.core.genes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneService {

    private final GeneRepository geneRepository;

    public Gene getGene(int id) {
        return geneRepository.findById(id).orElse(null);
    }

    public Gene save(Gene gene) {
        return geneRepository.save(gene);
    }

    public List<Gene> saveAll(List<Gene> gene) {
        return geneRepository.saveAll(gene);
    }

    public List<Gene> fuzzyFindByName(String name) {
        return geneRepository.fuzzyFindByName(name);
    }

    public List<Gene> fuzzyRelatedGenes(String name) {
        return geneRepository.findRelatedGenes(name);
    }

}
