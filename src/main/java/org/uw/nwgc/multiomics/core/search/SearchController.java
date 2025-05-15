package org.uw.nwgc.multiomics.core.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.genes.GeneService;
import org.uw.nwgc.multiomics.core.project.ProjectService;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

    private final SampleService sampleService;
    private final ProjectService projectService;
    private final GeneService geneService;

    @Autowired
    public SearchController(SampleService sampleService, ProjectService projectService, GeneService geneService) {
        this.sampleService = sampleService;
        this.projectService = projectService;
        this.geneService = geneService;
    }

    @GetMapping("/search/{query}")
    private ResponseEntity<List<SearchResponseDTO>> search(@PathVariable String query) {
        List<SearchResponseDTO> response = new ArrayList<>();
        SearchResponseDTO genes = new SearchResponseDTO("Genes");
        genes.addAll(geneService.fuzzyFindByName(query));
        response.add(genes);

        SearchResponseDTO samples = new SearchResponseDTO("Samples");
        samples.addAll(sampleService.fuzzyFindByName(query, 5));
        response.add(samples);

        SearchResponseDTO projects = new SearchResponseDTO("Projects");
        projects.addAll(projectService.fuzzyFindByName(query, 5));
        response.add(projects);

        return ResponseEntity.ok(response);
    }

}
