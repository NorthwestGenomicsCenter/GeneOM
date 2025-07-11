package org.uw.nwgc.multiomics.core.project;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private static UUID uuid1 = UUID.randomUUID();
    private static UUID uuid2 = UUID.randomUUID();
    private static UUID uuid3 = UUID.randomUUID();

    public List<ProjectDTO> getAllProjects() {
        List<ProjectDTO> projects = new ArrayList<ProjectDTO>();

        // Dummy data, normally pulled from DB
        return List.of(
                new ProjectDTO(uuid1, "Project 1", "This is test project 1. It serves to help test the Projects view.", 10),
                new ProjectDTO(uuid2, "Project 2", "This is test project 2. It serves to help test the Projects view.", 20),
                new ProjectDTO(uuid3, "Project 3", "This is test project 3. It serves to help test the Projects view.", 30)
        );
    }
}
