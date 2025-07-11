package org.uw.nwgc.multiomics.core.project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();

        if (projects.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/all/technologies")
    public ResponseEntity<Map<UUID, List<TechnologyDTO>>> getAllProjectsIncludeTechnologies() {
        Map<UUID, List<TechnologyDTO>> technologiesMap = new HashMap<>();
        List<ProjectDTO> projects = projectService.getAllProjects();

        if (projects.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

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
}
