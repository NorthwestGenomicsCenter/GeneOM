package org.uw.nwgc.multiomics.core.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SampleController {

    private final SampleService sampleService;
    private final SampleRepository sampleRepository;

    @Autowired
    public SampleController(SampleService sampleService, SampleRepository sampleRepository) {
        this.sampleService = sampleService;
        this.sampleRepository = sampleRepository;
    }

    @GetMapping("/sample/search/{name}")
    public ResponseEntity<List<Sample>> searchSampleByName(@PathVariable String name) {
        List<Sample> samples = sampleService.fuzzyFindByName(name, 5);

        if (samples.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(samples);
    }

    @PostMapping("/sample/save")
    public ResponseEntity<Sample> save(@RequestBody SampleDTO sampleDTO) {
        Sample sample;
        if (sampleDTO.getId() == null) {
            sample = new Sample();
        } else {
            sample = sampleService.getById(sampleDTO.getId());
        }
        sample.update(sampleDTO);
        sample = sampleRepository.save(sample);

        return ResponseEntity.ok(sample);
    }
}
