package org.uw.nwgc.multiomics.core.technology;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.project.ProjectDTO;
import org.uw.nwgc.multiomics.core.sample.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TechnologyService {

    private TagRepository tagRepository;
    private SampleRepository sampleRepository;

    public TechnologyService(TagRepository tagRepository, SampleRepository sampleRepository) {
        this.tagRepository = tagRepository;
        this.sampleRepository = sampleRepository;
    }

    public List<TechnologyDTO> getAllTechnologies() {
        List<TechnologyDTO> technologies = new ArrayList<>();

        List<Tag> technologyTags = tagRepository.findByLabel("Technology");
        for (Tag technologyTag : technologyTags) {
            long sampleCount = sampleRepository.countByTags_Id(technologyTag.getId());
            technologies.add(new TechnologyDTO(technologyTag.getId(), technologyTag.getValue(), sampleCount));
        }

        return technologies;
        /*
        // Dummy data, normally pulled from DB
        return List.of(
                new TechnologyDTO(UUID.randomUUID(), "Technology 1", "This is test technology 1. It serves to help test the Technologies view.", 10),
                new TechnologyDTO(UUID.randomUUID(), "Technology 2", "This is test technology 2. It serves to help test the Technologies view.", 20),
                new TechnologyDTO(UUID.randomUUID(), "Technology 3", "This is test technology 3. It serves to help test the Technologies view.", 30)
        );

         */
    }

    public List<TechnologyDTO> getTechnologiesForProject(UUID projectId) {
        // Get all samples that are tagged with the given project
        List<Sample> samplesWithProject = sampleRepository.findByTags_Id(projectId);

        // Create a frequency map of technology tag IDs
        Map<UUID, Long> technologyCountMap = samplesWithProject.stream()
                .flatMap(sample -> sample.getTags().stream()
                        .filter(tag -> tag.getLabel().equalsIgnoreCase("TECHNOLOGY")))
                .collect(Collectors.groupingBy(Tag::getId, Collectors.counting()));

        // Map distinct technology tags into DTOs with their sample counts
        return samplesWithProject.stream()
                .flatMap(sample -> sample.getTags().stream())
                .filter(tag -> tag.getLabel().equalsIgnoreCase("TECHNOLOGY"))
                .distinct()
                .map(tag -> new TechnologyDTO(
                        tag.getId(),
                        tag.getValue(),
                        technologyCountMap.getOrDefault(tag.getId(), 0L)
                ))
                .toList();

        /*
        // Dummy data, normally pulled from DB
        return List.of(
                new TechnologyDTO(UUID.randomUUID(), "Technology 1", "This is test technology 1. It serves to help test the Projects view.", 5),
                new TechnologyDTO(UUID.randomUUID(), "Technology 2", "This is test technology 2. It serves to help test the Projects view.", 10),
                new TechnologyDTO(UUID.randomUUID(), "Technology 3", "This is test technology 3. It serves to help test the Projects view.", 15),
                new TechnologyDTO(UUID.randomUUID(), "Technology 4", "This is test technology 4. It serves to help test the Projects view.", 10),
                new TechnologyDTO(UUID.randomUUID(), "Technology 5", "This is test technology 5. It serves to help test the Projects view.", 15)
        );

         */
    }
}
