package org.uw.nwgc.multiomics.core.analysis;

import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.analysis.dto.*;
import org.uw.nwgc.multiomics.core.cohort.Cohort;
import org.uw.nwgc.multiomics.core.cohort.CohortService;
import reactor.core.publisher.Flux;
import org.uw.nwgc.multiomics.core.analysis.LayoutUpdatePublisher.AnalysisSSEUpdateDTO;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AnalysisMapper analysisMapper;
    private final CohortService cohortService;
    private final LayoutUpdatePublisher updates;

    public AnalysisController(
            AnalysisService analysisService,
            AnalysisMapper analysisMapper,
            CohortService cohortService,
            LayoutUpdatePublisher updates
    ) {
        this.analysisService = analysisService;
        this.analysisMapper = analysisMapper;
        this.cohortService = cohortService;
        this.updates = updates;
    }

    @PostMapping(path = "/update/locus/{id}")
    public ResponseEntity<Void> updateLocus(@PathVariable UUID id, @RequestBody AnalysisSSEUpdateDTO body) {
        updates.publish("locus_" + id, body);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/locus/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamLocus(@PathVariable UUID id) {
        Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(t -> "heartbeat " + LocalTime.now());
        return Flux.merge(heartbeat, updates.flux("locus_" + id));
    }

    @GetMapping(path = "/flux/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux(@PathVariable UUID id) {
        Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(t -> "heartbeat " + LocalTime.now());
        return Flux.merge(heartbeat, updates.flux(id.toString()));
    }

    @Transactional
    @GetMapping("/reference")
    public ResponseEntity<CustomReference> reference() {
        return ResponseEntity.ok(new CustomReference());
    }

    @Transactional
    @PostMapping("/update/layout/{id}")
    public ResponseEntity<Void> updateLayout(@PathVariable UUID id, @RequestBody AnalysisSSEUpdateDTO newLayout) {
        boolean updated = analysisService.updateLayout(id, newLayout.message());
        if (updated) {
            updates.publish(id.toString(), newLayout);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/update/resources/{id}")
    public ResponseEntity<Void> updateResources(@PathVariable UUID id, @RequestBody AnalysisSSEUpdateDTO resources) {
        boolean updated = analysisService.updateSelectedResources(id, resources.message());
//        if (updated) {
//            updates.publish(id.toString(), resources);
//        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public Collection<Analysis> getAll() {
        return analysisService.getAll();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<AnalysisViewDTO> viewAnalysis(@PathVariable UUID id) throws InterruptedException {
        Optional<Analysis> analysisQuery = analysisService.findById(id);
        if (analysisQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Analysis analysis = analysisQuery.get();
        AnalysisViewDTO dto = analysisMapper.toViewDTO(analysis);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<AnalysisEditDTO> editAnalysis(@PathVariable UUID id) {
        return ResponseEntity.ok(analysisService.getEditDTO(id));
    }

    @GetMapping("/recent")
    public ResponseEntity<Collection<RecentAnalysisDTO>> recent() {
        Collection<RecentAnalysisDTO> recent = analysisService.getRecent();
        return ResponseEntity.ok(recent);
    }

    @Transactional
    @PostMapping("/add/samples")
    public ResponseEntity<AnalysisEditDTO> addSamples(@RequestBody AddAnalysisSampleDTO dto) {
        analysisService.addSamples(dto);
        return ResponseEntity.ok(analysisService.getEditDTO(dto.getAnalysisId()));
    }

    @Transactional
    @PostMapping("/create")
    public Analysis create(@RequestBody CreateAnalysisDTO dto) {
        return analysisService.createNew(dto);
    }

    @Transactional
    @PostMapping("/create/customReferenceGenome/{id}")
    public Analysis createCustomReferenceGenome(@PathVariable UUID id, @RequestBody CreateCustomReferenceGenomeDTO dto) {
        return analysisService.createNewCustomReferenceGenome(id, dto);
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        analysisService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/add-cohort")
    public ResponseEntity<?> createCustomReferenceGenome(@RequestBody AddAnalysisCohortDTO dto) {
        Optional<Analysis> analysisQuery = analysisService.findById(dto.analysisId());
        Optional<Cohort> cohortQuery = cohortService.getById(dto.cohortId());
        if (analysisQuery.isEmpty() ||  cohortQuery.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var analysis = analysisQuery.get();
        var cohort = cohortQuery.get();

        analysis.getCohorts().add(cohort);
        analysis = analysisService.save(analysis);

        return ResponseEntity.ok(analysisMapper.toViewDTO(analysis));
    }
}
