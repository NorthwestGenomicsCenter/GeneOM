package org.uw.nwgc.multiomics.core.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/omic")
public class OmicController {

    public record OmicResourceDto(UUID sampleid, String path, String type) {};
    private final OmicService omicService;
    private final SampleService sampleService;

    public OmicController(
            OmicService omicService,
            SampleService sampleService
    ) {
        this.omicService = omicService;
        this.sampleService = sampleService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveOmics(@RequestBody OmicResourceDto dto) {
        System.out.println("dto = " + dto);
        Optional<Sample> sample = sampleService.getById(dto.sampleid);
        if (sample.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OmicResource resource = new OmicResource(dto);
        OmicResource newResource = omicService.save(resource);

        return ResponseEntity.ok("Saved");
    }

}
