package org.uw.nwgc.multiomics.core.genes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GeneController {

    private final GeneService geneService;

    @Autowired
    public GeneController(GeneService geneService) {
        this.geneService = geneService;
    }

    @GetMapping("/gene/{id}")
    public ResponseEntity<Gene> getGeneById(@PathVariable int id) {
        Gene gene = geneService.getGene(id);
        if (gene == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(gene);
    }

    @GetMapping("/gene/related/{name}")
    public ResponseEntity<List<Gene>> relatedGenes(@PathVariable String name) {
        List<Gene> gene = geneService.fuzzyRelatedGenes(name);
        if (gene == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(gene);
    }

    @GetMapping("/gene/search/{name}")
    public ResponseEntity<List<SearchResponse>> searchGenesByName(@PathVariable String name) {

        List<SearchResponse> responses = new ArrayList<>();

        SearchResponse projects = new SearchResponse("projects");
        var project1 = new Project("Liver Project", 1, 2);
        var project2 = new Project("Brain Project", 1, 4);
        projects.add(project1);
        projects.add(project2);
        responses.add(projects);

        // Make test samples
        SearchResponse samples = new SearchResponse("samples");
        samples.add(new Sample("Liver Sample 1", 1, project1));
        samples.add(new Sample("Liver Sample 2", 2, project1));

        samples.add(new Sample("Brain Sample 1", 3, project1));
        samples.add(new Sample("Brain Sample 2", 4, project1));
        samples.add(new Sample("Brain Sample 3", 5, project1));
        samples.add(new Sample("Brain Sample 4", 6, project1));
        responses.add(samples);

        List<Gene> gene = geneService.fuzzyFindByName(name);
        SearchResponse genes = new SearchResponse("genes");
        genes.addAll(gene);
        responses.add(genes);

        if (gene.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(responses);
    }

    public static class SearchResponse {
        public String label;
        public List<Object> items = new ArrayList<>();

        public SearchResponse(String label) {
            this.label = label;
        }

        public void add(Object o) {
            this.items.add(o);
        }

        public void addAll(List objs) {
            this.items.addAll(objs);
        }
    }

    public static class Sample {
        public Integer id;
        public String name;
        public Project project;

        public Sample(String name, Integer id, Project project) {
            this.name = name;
            this.id = id;
            this.project = project;
        }
    }

    public static class Project {
        public Integer id;
        public String name;
        public Integer samplesCount;

        public Project(String name, Integer id, Integer samplesCount) {
            this.name = name;
            this.id = id;
            this.samplesCount = samplesCount;
        }
    }

}
