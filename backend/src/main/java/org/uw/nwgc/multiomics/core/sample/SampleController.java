package org.uw.nwgc.multiomics.core.sample;

import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sample")
public class SampleController {

    private final SampleService sampleService;

    @Autowired
    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
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
                .map(SampleDTO::new)
                .toList();

        return ResponseEntity.ok(sampleDTOS);
    }


    @PostMapping("/save")
    public ResponseEntity<Sample> save(@RequestBody SampleDTO sampleDTO) throws AuthException {
        Sample sample = sampleService.save(sampleDTO);
        return ResponseEntity.ok(sample);
    }

    @Transactional
    @DeleteMapping("/tag/{tagId}")
    public ResponseEntity<Sample> deleteTag(@RequestBody SampleDTO sampleDTO, @PathVariable UUID tagId) throws AuthException {
        Sample sample = sampleService.deleteTag(sampleDTO, tagId);
        return ResponseEntity.ok(sample);
    }

}
