package org.uw.nwgc.multiomics.core.resources;

import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
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
    private final List<VizTransformer> transformers;

    public OmicController(
            OmicService omicService,
            SampleService sampleService,
            List<VizTransformer> transformers
    ) {
        this.omicService = omicService;
        this.sampleService = sampleService;
        this.transformers = transformers;
    }

    @GetMapping("/transformers")
    public ResponseEntity<List<String>> getRegisteredTransformers() {
        return ResponseEntity.ok(transformers
                .stream()
                .map(VizTransformer::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.toList())
        );
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

    @GetMapping("/igv/{resourceId}/{filename}")
    public ResponseEntity<ResourceRegion> getResource(
            @RequestHeader HttpHeaders headers,
            @PathVariable UUID resourceId,
            @PathVariable String filename
    ) throws IOException {
        OmicResource resource = omicService.findById(resourceId);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = FileUtil.resolvePath(resource.getPath());
        if (filename.endsWith("bai")) {
            path = Path.of(path.toString() + ".bai");
        }

        Resource file = new FileSystemResource(path);
        long contentLength = file.contentLength();

        ResourceRegion region = getResourceRegion(file, headers, contentLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(file).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    private ResourceRegion getResourceRegion(Resource resource, HttpHeaders headers, long contentLength) throws IOException {
        final long chunkSize = 1024 * 1024; // 1MB
        if (headers.getRange().isEmpty()) {
            return new ResourceRegion(resource, 0, contentLength);
        }

        HttpRange range = headers.getRange().get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(chunkSize, end - start + 1);

        return new ResourceRegion(resource, start, rangeLength);
    }

    @GetMapping("/visualize/{resourceId}")
    public ResponseEntity<?> getResource(@PathVariable UUID resourceId, @RequestHeader HttpHeaders header) throws
            InstantiationException,
            IllegalAccessException,
            NoSuchMethodException,
            InvocationTargetException {
        OmicResource resource = omicService.findById(resourceId);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        VizTransformer transformer = resource.getVizTransformer()
                .get(0)
                .getDeclaredConstructor()
                .newInstance();

        try (Reader fileReader = FileUtil.readResourcePath(resource.getPath())) {
            return ResponseEntity.ok(transformer.serveResource(fileReader, header));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/validate/{resourceId}")
    public ResponseEntity<?> validateResource(@PathVariable UUID resourceId) throws
            InstantiationException,
            IllegalAccessException,
            NoSuchMethodException,
            InvocationTargetException {
        OmicResource resource = omicService.findById(resourceId);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        VizTransformer transformer = resource.getVizTransformer()
                .get(0)
                .getDeclaredConstructor()
                .newInstance();

        try (Reader fileReader = FileUtil.readResourcePath(resource.getPath())) {
            return ResponseEntity.ok(transformer.validateResource(fileReader));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
