package org.uw.nwgc.multiomics.core.analysis;

import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uw.nwgc.multiomics.core.analysis.LayoutUpdatePublisher.AnalysisSSEUpdateDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.*;
import org.uw.nwgc.multiomics.core.cohort.Cohort;
import org.uw.nwgc.multiomics.core.cohort.CohortService;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.user.CurrentUserResolver;
import org.uw.nwgc.multiomics.core.user.User;
import org.uw.nwgc.multiomics.util.coordinates_reader.GenomicFileParser;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.ParsedGenomicDataDTO;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisMapper analysisMapper;
    private final AnalysisService analysisService;
    private final CohortService cohortService;
    private final CurrentUserResolver currentUserResolver;
    private final LayoutUpdatePublisher updates;

    public AnalysisController(
            AnalysisService analysisService,
            AnalysisMapper analysisMapper,
            CohortService cohortService,
            CurrentUserResolver currentUserResolver,
            LayoutUpdatePublisher updates
    ) {
        this.analysisService = analysisService;
        this.analysisMapper = analysisMapper;
        this.cohortService = cohortService;
        this.currentUserResolver = currentUserResolver;
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

    @GetMapping ("/available")
    public Collection<AvailableUserAnalysisDTO> getAvailableUserAnalysis() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();

        return analysisService.getAvailableUserAnalysisDTOs(user);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<AnalysisViewDTO> viewAnalysis(@PathVariable UUID id) throws InterruptedException {
        Optional<Analysis> analysisQuery = analysisService.findById(id);
        if (analysisQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Analysis analysis = analysisQuery.get();

        analysis.setLastModified(LocalDateTime.now());
        analysis = analysisService.save(analysis);

        AnalysisViewDTO dto = analysisMapper.toViewDTO(analysis);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<AnalysisEditDTO> editAnalysis(@PathVariable UUID id) {
        return ResponseEntity.ok(analysisService.getEditDTO(id));
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateAnalysis(@PathVariable UUID id, @RequestBody UpdateAnalysisDTO dto) {
        Optional<Analysis> analysisQuery = analysisService.findById(id);
        if (analysisQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        analysisService.updateAnalysis(analysisQuery.get(), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<RecentAnalysisDTO> recent() {
        RecentAnalysisDTO recent = analysisService.getRecent();
        return ResponseEntity.ok(recent);
    }

    @Transactional
    @PostMapping("/add/samples")
    public ResponseEntity<AnalysisEditDTO> addSamples(@RequestBody AddAnalysisSampleDTO dto) {
        analysisService.addSamples(dto);
        return ResponseEntity.ok(analysisService.getEditDTO(dto.getAnalysisId()));
    }

    @Transactional
    @PostMapping("/add/users")
    public ResponseEntity<AnalysisEditDTO> addUsers(@RequestBody AddAnalysisUsersDTO dto) {
        analysisService.addUsers(dto);
        return ResponseEntity.ok(analysisService.getEditDTO(dto.getAnalysisId()));
    }

    @Transactional
    @PostMapping("/create")
    public Analysis create(@RequestBody CreateAnalysisDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        return analysisService.createNew(dto, user);
    }

    @Transactional
    @PostMapping("/create/customReferenceGenome/{id}")
    public Analysis createCustomReferenceGenome(@PathVariable UUID id, @RequestBody CreateCustomReferenceGenomeDTO dto) {
        return analysisService.createNewCustomReferenceGenome(id, dto);
    }

    @Transactional
    @DeleteMapping("/{id}")
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

    @Transactional
    @DeleteMapping("/{id}/{sampleId}")
    public ResponseEntity<?> deleteSample(@PathVariable UUID id, @PathVariable UUID sampleId) {
        Optional<Analysis> analysisQuery = analysisService.findById(id);
        if (analysisQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Analysis analysis = analysisQuery.get();
        Optional<Sample> sample = analysis.getSamples()
                .stream()
                .filter(s -> s.getId().equals(sampleId))
                .findFirst();

        if (sample.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        analysis.getSamples().remove(sample.get());
        analysis = analysisService.save(analysis);

        return ResponseEntity.ok(analysisMapper.toViewDTO(analysis));
    }

    @PostMapping("/import/coordinates")
    public ResponseEntity<?> importCoordinates(@RequestParam("null") MultipartFile file) throws IOException {
        try (GenomicFileParser parser = GenomicFileParser.from(file)) {
            return ResponseEntity.ok(parser.detectColumns());
        }
    }

    @PostMapping("/import/tabular-data")
    public ResponseEntity<?> importTabularDataFile(@RequestParam("null") MultipartFile file) throws IOException {
        try (GenomicFileParser parser = GenomicFileParser.from(file)) {

            ParsedGenomicDataDTO pd = new ParsedGenomicDataDTO(file.getName(), parser.parseRecords());
            return ResponseEntity.ok(pd);
        }
    }

}
