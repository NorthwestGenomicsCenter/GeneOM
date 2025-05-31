package org.uw.nwgc.multiomics.core.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sample")
public class SampleController {

    private final SampleService sampleService;
    private final SampleRepository sampleRepository;

    @Autowired
    public SampleController(SampleService sampleService, SampleRepository sampleRepository) {
        this.sampleService = sampleService;
        this.sampleRepository = sampleRepository;
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
    public ResponseEntity<List<Sample>> selectAll() {
        return ResponseEntity.ok(sampleService.getAll());
    }

    @PostMapping("/save")
    public ResponseEntity<Sample> save(@RequestBody SampleDTO sampleDTO) {
        Sample sample = new Sample();
        if (sampleDTO.getId() != null) {
            Optional<Sample> optionalSample = sampleService.getById(sampleDTO.getId());
            if (optionalSample.isPresent()) {
                sample = optionalSample.get();
            }
        }

        sample.update(sampleDTO);
        sample = sampleRepository.save(sample);

        return ResponseEntity.ok(sample);
    }
}
