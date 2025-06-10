package org.uw.nwgc.multiomics.core.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/omic")
public class OmicController {

    private final OmicService omicService;
    private final SampleService sampleService;

    public OmicController(
            OmicService omicService,
            SampleService sampleService
    ) {
        this.omicService = omicService;
        this.sampleService = sampleService;
    }

    @GetMapping("/visualizers/{sampleId}")
    public ResponseEntity<?> getOmicResource(@PathVariable UUID sampleId) {
        Optional<Sample> sampleOptional = sampleService.getById(sampleId);
        if (sampleOptional.isPresent()) {
            Sample sample = sampleOptional.get();
            Set<OmicResource> resources = sample.getOmicResources();
            Set<String> components = resources.stream()
                    .map(OmicResource::getVisualizers)
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(components);
        }

        return ResponseEntity.notFound().build();
    }


    @PostMapping("/save")
    public ResponseEntity<?> saveOmics(@RequestBody OmicResourceDTO dto) {
        Optional<Sample> sample = sampleService.getById(dto.getSampleid());
        if (sample.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OmicResource resource = new OmicResource(dto, sample.get());
        sample.get().getOmicResources().add(resource);
        OmicResource newResource = omicService.save(resource);

        return ResponseEntity.ok("Saved");
    }

}
