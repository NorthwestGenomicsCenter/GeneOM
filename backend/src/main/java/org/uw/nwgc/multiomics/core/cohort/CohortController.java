package org.uw.nwgc.multiomics.core.cohort;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/cohort")
public class CohortController {

    private final CohortService cohortService;
    private final SampleService sampleService;
    private final CohortRepository cohortRepository;

    public CohortController(
            CohortService cohortService,
            SampleService sampleService,
            CohortRepository cohortRepository) {
        this.cohortService = cohortService;
        this.sampleService = sampleService;
        this.cohortRepository = cohortRepository;
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

    @Transactional
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

    @Transactional
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
}
