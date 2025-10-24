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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uw.nwgc.multiomics.assay.DiffExpressionScatterVisualizer;
import org.uw.nwgc.multiomics.core.resources.dto.AddOmicsResourceDTO;
import org.uw.nwgc.multiomics.core.resources.dto.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
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

    @GetMapping("/visualize/{resourceId}/{filename}")
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
        path = omicService.findIndexFilePath(filename, path);
        if (path == null) {
            return ResponseEntity.notFound().build();
        }

        Resource file = new FileSystemResource(path);
        long contentLength = file.contentLength();

        // Don't serve a file larger than 300MB with this method
        // This number was selected to be twice what was observed
        // downloading in the production environment
        final long chunkSize = 1024 * 1024; // 1MB
        HttpRange range = headers.getRange().getFirst();
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = end - start + 1;
        if (rangeLength >= (300 * chunkSize)) {
            return ResponseEntity.badRequest().build();
        }

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

    @GetMapping("/visualize/scatter")
    public ResponseEntity<?> getResourceScatter(@RequestHeader HttpHeaders header) {
        final String TEST_FILE_PATH = "/net/nwgc/vol1/techdev/multiomics-browser/carbone_diff_expression.csv";

        VizTransformer transformer = new DiffExpressionScatterVisualizer();
        try (Reader fileReader = FileUtil.readResourcePath(TEST_FILE_PATH)) {
            return ResponseEntity.ok(transformer.serveResource(fileReader, header));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<?> saveOmics(@RequestBody AddOmicsResourceDTO[] dtos) {
        for (AddOmicsResourceDTO dto : dtos) {
            Optional<Sample> sample = sampleService.getById(dto.getSampleId());
            if (sample.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            OmicResource resource = new OmicResource(dto, sample.get());
            sample.get().getOmicResources().add(resource);
            OmicResource newResource = omicService.save(resource);
        }

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

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("sampleId") UUID sampleId) throws IOException {
        Optional<Sample> sample = sampleService.getById(sampleId);
        if (sample.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OmicResource resource = omicService.uploadFile(file, sample.get());

        return ResponseEntity.ok(new OmicResourceDTO(resource));
    }

}
