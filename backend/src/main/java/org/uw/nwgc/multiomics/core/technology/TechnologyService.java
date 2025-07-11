package org.uw.nwgc.multiomics.core.technology;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.project.ProjectDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TechnologyService {

    public List<TechnologyDTO> getAllTechnologies() {
        List<TechnologyDTO> technologies = new ArrayList<>();
        
        // Dummy data, normally pulled from DB
        return List.of(
                new TechnologyDTO(UUID.randomUUID(), "Technology 1", "This is test technology 1. It serves to help test the Technologies view.", 10),
                new TechnologyDTO(UUID.randomUUID(), "Technology 2", "This is test technology 2. It serves to help test the Technologies view.", 20),
                new TechnologyDTO(UUID.randomUUID(), "Technology 3", "This is test technology 3. It serves to help test the Technologies view.", 30)
        );
    }

    public List<TechnologyDTO> getTechnologiesForProject(UUID projectId) {
        List<TechnologyDTO> technologies = new ArrayList<>();

        // Dummy data, normally pulled from DB
        return List.of(
                new TechnologyDTO(UUID.randomUUID(), "Technology 1", "This is test technology 1. It serves to help test the Projects view.", 5),
                new TechnologyDTO(UUID.randomUUID(), "Technology 2", "This is test technology 2. It serves to help test the Projects view.", 10),
                new TechnologyDTO(UUID.randomUUID(), "Technology 3", "This is test technology 3. It serves to help test the Projects view.", 15),
                new TechnologyDTO(UUID.randomUUID(), "Technology 4", "This is test technology 4. It serves to help test the Projects view.", 10),
                new TechnologyDTO(UUID.randomUUID(), "Technology 5", "This is test technology 5. It serves to help test the Projects view.", 15)
        );
    }
}
