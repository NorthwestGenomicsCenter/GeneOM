package org.uw.nwgc.multiomics.core.technology;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/technology")
public class TechnologyController {
    public TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TechnologyDTO>> getAllTechnologies() {
        List<TechnologyDTO> technologies = technologyService.getAllTechnologies();
        return ResponseEntity.ok(technologies);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllTechnologyNames() {
        List<String> technologyNames = technologyService.getAllNames();
        return ResponseEntity.ok(technologyNames);
    }

    @GetMapping("/{technologyId}")
    public ResponseEntity<TechnologyDTO> getTechnologyById(@PathVariable("technologyId") int technologyId) {
        return ResponseEntity.notFound().build();
    }
}
