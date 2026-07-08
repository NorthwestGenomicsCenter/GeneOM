package org.uw.nwgc.multiomics.core.sample;

import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uw.nwgc.multiomics.api.view.SampleViewDTO;
import org.uw.nwgc.multiomics.api.view.SampleViewService;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.sample.dto.AddExternalSampleResourcesDTO;
import org.uw.nwgc.multiomics.core.sample.dto.CreateSampleDTO;
import org.uw.nwgc.multiomics.core.sample.dto.CreateSampleFromExternalDTO;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;

import java.util.*;

@RestController
@RequestMapping("/sample")
public class SampleController {

    private final SampleService sampleService;
    private final SampleMapper sampleMapper;
    private final SampleRepository sampleRepository;
    private final SampleViewService sampleViewService;

    @Autowired
    public SampleController(
            SampleService sampleService,
            SampleMapper sampleMapper,
            SampleRepository sampleRepository,
            SampleViewService sampleViewService
    ) {
        this.sampleService = sampleService;
        this.sampleMapper = sampleMapper;
        this.sampleRepository = sampleRepository;
        this.sampleViewService = sampleViewService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SampleDTO> editSampleInformation(@PathVariable UUID uuid) {
        Optional<Sample> optional = sampleService.getById(uuid);
        if  (optional.isPresent()) {
            Sample sample = optional.get();
            return ResponseEntity.ok(sampleMapper.toDTO(sample));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Sample>> searchSampleByName(@PathVariable String name) {
        List<Sample> samples = sampleService.fuzzyFindByName(name, 5);

        if (samples.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(samples);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SampleDTO>> selectAll() {
        List<SampleDTO> sampleDTOS = sampleService.getAll()
                .stream()
                .map(sampleMapper::toDTO)
                .toList();

        return ResponseEntity.ok(sampleDTOS);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SampleDTO>> getSamplesWithFilter(
            @RequestParam(required = false) List<UUID> projectIds,
            @RequestParam(required = false) List<UUID> technologyIds
    ) {
        List<SampleDTO> samples = sampleService.findSamplesByCriteria(projectIds, technologyIds);
        if (samples.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(samples);
    }

    @PostMapping("/save")
    public ResponseEntity<Sample> save(@RequestBody SampleDTO sampleDTO) throws AuthException {
        Sample sample = sampleService.save(sampleDTO);
        return ResponseEntity.ok(sample);
    }

    @PostMapping("/create")
    public ResponseEntity<Sample> create(@RequestBody CreateSampleDTO sampleDTO) throws AuthException {
        Sample sample = sampleService.create(sampleDTO);
        return ResponseEntity.ok(sample);
    }

    @PostMapping("/create-external-sample")
    public ResponseEntity<Sample> createExternalSample(@RequestBody CreateSampleFromExternalDTO sampleDTO) throws AuthException {
        Sample sample = sampleService.createExternalSample(sampleDTO);
        return ResponseEntity.ok(sample);
    }

    @PostMapping("/add-resources")
    public ResponseEntity<Sample> addExternalSampleResources(@RequestBody AddExternalSampleResourcesDTO externalSampleResourcesDTO) throws AuthException {
        Sample sample = sampleService.addResourcesToExternalSample(externalSampleResourcesDTO);
        return ResponseEntity.ok(sample);
    }

    @Transactional
    @DeleteMapping("/tag/{tagId}")
    public ResponseEntity<Sample> deleteTag(@RequestBody SampleDTO sampleDTO, @PathVariable UUID tagId) throws AuthException {
        Sample sample = sampleService.deleteTag(sampleDTO, tagId);
        return ResponseEntity.ok(sample);
    }

//    @GetMapping("/{sampleId}/views")
//    public List<SampleViewDTO> getSampleViews(@PathVariable String sampleId) {
//        return sampleViewService.getAvailableTechnologyViewPairsForSample(sampleId);
//    }

    @GetMapping("/{sampleId}/views")
    public ResponseEntity<List<SampleViewDTO>> getSampleViews(@PathVariable UUID sampleId) {
        Optional<Sample> sample = sampleService.getById(sampleId);
        if (!sample.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Sample s = sample.get();
        Set<OmicResource> resources = s.getOmicResources();
        List<SampleViewDTO> views = resources.stream()
                .map(SampleViewDTO::mapResource)
                .flatMap(List::stream)
                .toList();
        return ResponseEntity.ok(views);

    }


    @GetMapping("/{sampleId}/tviews")
    public ResponseEntity<Map<String, List<String>>> getAvailableViewsByTechnologyForSample(
            @PathVariable String sampleId
    ) {
        Map<String, List<String>> result = sampleViewService.getAvailableViewsByTechnologyForSample(sampleId);
        return ResponseEntity.ok(result);
    }
}
