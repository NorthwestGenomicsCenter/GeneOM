package org.uw.nwgc.multiomics.core.cohort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;
import org.uw.nwgc.multiomics.core.sample.SampleRepository;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cohort")
public class CohortController {

    private final CohortService cohortService;

    public CohortController(CohortService cohortService) {
        this.cohortService = cohortService;
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
}
