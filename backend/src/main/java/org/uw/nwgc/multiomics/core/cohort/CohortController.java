package org.uw.nwgc.multiomics.core.cohort;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uw.nwgc.multiomics.core.cohort.dto.AddCohortResourceDTO;
import org.uw.nwgc.multiomics.core.cohort.dto.AddCohortSampleDTO;
import org.uw.nwgc.multiomics.core.resources.OmicService;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/cohort")
@Transactional
public class CohortController {

    private final CohortService cohortService;
    private final SampleService sampleService;
    private final OmicService omicService;
    private final CohortRepository cohortRepository;

    public CohortController(
            CohortService cohortService,
            SampleService sampleService,
            CohortRepository cohortRepository,
            OmicService omicService) {
        this.cohortService = cohortService;
        this.sampleService = sampleService;
        this.cohortRepository = cohortRepository;
        this.omicService = omicService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Cohort>> selectAll() {
        return ResponseEntity.ok(cohortService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Cohort> save(@RequestBody CohortDTO cohortDto) {
        Cohort cohort = new Cohort(cohortDto);
        cohortService.create(cohort);

        return ResponseEntity.ok(cohort);
    }

    @PostMapping("/{cohortId}/update")
    public ResponseEntity<Cohort> update(@PathVariable UUID cohortId, @RequestBody CohortDTO cohortDto) {
        Optional<Cohort> cohort = cohortService.update(cohortId, cohortDto);

        return cohort.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/{cohortId}")
    public ResponseEntity<CohortDTO> getCohort(@PathVariable UUID cohortId) {
        Optional<Cohort> cohort = cohortService.getById(cohortId);
        if (cohort.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new CohortDTO(cohort.get()));
    }

    @GetMapping("/{cohortId}/samples")
    public ResponseEntity<Collection<Sample>> getCohortSamples(@PathVariable UUID cohortId) {
        Optional<Cohort> cohort = cohortService.getById(cohortId);
        if (cohort.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<Sample> samples = cohort.get().getSamples();

        return ResponseEntity.ok(samples);
    }

    @PostMapping("/add/samples")
    public ResponseEntity<Cohort> addCohortSamples(@RequestBody AddCohortSampleDTO addCohortSampleDTO) {
        Optional<Cohort> cohortQuery = cohortService.getById(addCohortSampleDTO.getCohortId());
        List<UUID> sampleIds = addCohortSampleDTO
                .getSamples()
                .stream().map(SampleDTO::getId)
                .toList();
        Optional<List<Sample>> samplesQuery = sampleService.findByIds(sampleIds);

        if (cohortQuery.isEmpty() || samplesQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cohort cohort = cohortQuery.get();
        List<Sample> samples = samplesQuery.get();
        if (samples.size() != sampleIds.size()) {
            return ResponseEntity.badRequest().build();
        }

        cohort.getSamples().addAll(samples);
        cohortRepository.save(cohort);

        return ResponseEntity.ok(cohort);
    }

    @PostMapping("/upload-resource/{cohortId}")
    public ResponseEntity<Cohort> addCohortResource(
            @RequestParam("file") MultipartFile file,
            @PathVariable("cohortId") UUID cohortId
    ) throws IOException {
        Optional<Cohort> cohortQuery = cohortService.getById(cohortId);

        if (cohortQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cohort cohort = cohortQuery.get();

        omicService.uploadFile(file, cohort);
        cohortRepository.save(cohort);

        return ResponseEntity.ok(cohort);
    }
}
