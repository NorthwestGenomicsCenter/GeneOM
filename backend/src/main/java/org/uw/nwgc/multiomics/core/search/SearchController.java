package org.uw.nwgc.multiomics.core.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.genes.GeneService;
import org.uw.nwgc.multiomics.core.integrations.ExternalSampleDto;
import org.uw.nwgc.multiomics.core.integrations.IntegrationService;
import org.uw.nwgc.multiomics.core.integrations.SampleFileDto;
import org.uw.nwgc.multiomics.core.project.ProjectService;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

    private final SampleService sampleService;
    private final ProjectService projectService;
    private final GeneService geneService;
    private final IntegrationService integrationService;

    @Autowired
    public SearchController(
            SampleService sampleService,
            ProjectService projectService,
            GeneService geneService,
            IntegrationService integrationService
    ) {
        this.sampleService = sampleService;
        this.projectService = projectService;
        this.geneService = geneService;
        this.integrationService = integrationService;
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

    @GetMapping("/search/integrations/{query}")
    private ResponseEntity<List<SampleFileDto>> searchIntegrations(@PathVariable String query) {
        List<SampleFileDto> response = new ArrayList<>(integrationService.queryIntegration(query));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/integrations/sample/{query}")
    private ResponseEntity<List<ExternalSampleDto>> searchSampleIntegrations(@PathVariable String query) {
        List<ExternalSampleDto> response = new ArrayList<>(integrationService.querySampleIntegration(query));
        return ResponseEntity.ok(response);
    }

}
