package org.uw.nwgc.multiomics.core.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uw.nwgc.multiomics.core.technology.TechnologyDTO;
import org.uw.nwgc.multiomics.core.technology.TechnologyService;

import java.util.*;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;
    private final TechnologyService technologyService;

    public ProjectController(ProjectService projectService, TechnologyService technologyService) {
        this.projectService = projectService;
        this.technologyService = technologyService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ProjectDTO> projects = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllProjectNames() {
        List<String> projectNames = projectService.getAllNames();
        return ResponseEntity.ok(projectNames);
    }

    @GetMapping("/all/technologies")
    public ResponseEntity<Map<UUID, List<TechnologyDTO>>> getAllProjectsIncludeTechnologies(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Map<UUID, List<TechnologyDTO>> technologiesMap = new HashMap<>();
        Page<ProjectDTO> projects = projectService.getAllProjects(pageable);

        for (ProjectDTO current: projects) {
            UUID currentId = current.getId();
            technologiesMap.put(currentId, technologyService.getTechnologiesForProject(currentId));
        }

        return ResponseEntity.ok(technologiesMap);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable UUID projectId) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{projectId}/technologies")
    public ResponseEntity<List<TechnologyDTO>> getProjectTechnologies(@PathVariable UUID projectId) {
        List<TechnologyDTO> technologies = technologyService.getTechnologiesForProject(projectId);

        if (technologies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(technologies);
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.notFound().build();
    }
}
