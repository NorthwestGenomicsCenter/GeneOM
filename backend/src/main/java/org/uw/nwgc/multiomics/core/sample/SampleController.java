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
import org.uw.nwgc.multiomics.api.view.SampleViewDTO;
import org.uw.nwgc.multiomics.api.view.SampleViewService;

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
    private final SampleRepository sampleRepository;
    private final SampleViewService sampleViewService;

    @Autowired
    public SampleController(SampleService sampleService, SampleRepository sampleRepository, SampleViewService sampleViewService) {
        this.sampleService = sampleService;
        this.sampleRepository = sampleRepository;
        this.sampleViewService = sampleViewService;
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

//    @PostMapping("/save/tags/{sampleId}")
//    public ResponseEntity<String> saveTags(@PathVariable UUID sampleId, @RequestBody List<Tag.SampleTagDto> sampleTags) {
//        Optional<Sample> query = sampleService.getById(sampleId);
//        if (query.isEmpty()) {
//            return ResponseEntity.badRequest().body("Sample Not Found");
//        }
//
//        Sample sample = query.get();
//        Map<UUID, Tag> existingTags = sample.getTags()
//                .stream()
//                .collect(Collectors.toMap(Tag::getId, Function.identity()));
//
//        for (Tag.SampleTagDto tagDto : sampleTags) {
//            Tag tag = existingTags.computeIfAbsent(tagDto.id(), k -> new Tag());
//            tag.updateTag(tagDto);
//            sample.getTags().add(tag);
//        }
//
//        sampleService.save(sample);
//
//        return ResponseEntity.ok("Sample Saved");
//    }

    @GetMapping("/{sampleId}/views")
    public List<SampleViewDTO> getSampleViews(@PathVariable String sampleId) {
        return sampleViewService.getAvailableTechnologyViewPairsForSample(sampleId);
    }


    @GetMapping("/{sampleId}/tviews")
    public ResponseEntity<Map<String, List<String>>> getAvailableViewsByTechnologyForSample(
            @PathVariable String sampleId
    ) {
        Map<String, List<String>> result = sampleViewService.getAvailableViewsByTechnologyForSample(sampleId);
        return ResponseEntity.ok(result);
    }
}
