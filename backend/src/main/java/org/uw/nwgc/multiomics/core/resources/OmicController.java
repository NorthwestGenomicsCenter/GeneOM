package org.uw.nwgc.multiomics.core.resources;

import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.finder.FinderBasePath;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
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

    @GetMapping("/visualize/{resourceId}")
    public ResponseEntity<?> geResource(@PathVariable UUID resourceId) throws
            InstantiationException,
            IllegalAccessException,
            NoSuchMethodException,
            InvocationTargetException,
            IOException {
        OmicResource resource = omicService.findById(resourceId);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        VizTransformer transformer = resource.getVizTransformer()
                .getDeclaredConstructor()
                .newInstance();

        Object transformedResource = null;

        String relativePath = resource.getPath();
        if (relativePath.contains("://")) {
            String[] pathParts = relativePath.split("://");
            String basePath = FinderBasePath.fromString(pathParts[0]).getText();
            Path fullPath = Path.of(basePath, pathParts[1]);
            BufferedReader fileReader = Files.newBufferedReader(fullPath);
            transformedResource = transformer.serveResource(fileReader);
        }

        if (transformedResource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transformedResource);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveOmics(@RequestBody OmicResourceDTO dto) {
        Optional<Sample> sample = sampleService.getById(dto.getSampleId());
        if (sample.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OmicResource resource = new OmicResource(dto, sample.get());
        sample.get().getOmicResources().add(resource);
        OmicResource newResource = omicService.save(resource);

        return ResponseEntity.ok("Saved");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeOmic(@RequestBody OmicResourceDTO dto) throws AuthException {
        OmicResource resource = omicService.findById(dto.getOmicResourceId());
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        Sample sample = resource.getSample();
        sample.getOmicResources().remove(resource);

        sampleService.save(sample);
//        omicService.delete(resource);
        return ResponseEntity.ok("Deleted");
    }

}
