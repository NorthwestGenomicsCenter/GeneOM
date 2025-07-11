package org.uw.nwgc.multiomics.core.project;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.sample.SampleRepository;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.tag.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final TagRepository tagRepository;
    private final SampleRepository sampleRepository;

    private static UUID uuid1 = UUID.randomUUID();
    private static UUID uuid2 = UUID.randomUUID();
    private static UUID uuid3 = UUID.randomUUID();

    public ProjectService(TagRepository tagRepository, SampleRepository sampleRepository) {
        this.tagRepository = tagRepository;
        this.sampleRepository = sampleRepository;

    }

    public List<ProjectDTO> getAllProjects() {
        List<ProjectDTO> projects = new ArrayList<ProjectDTO>();

        List<Tag> projectTags = tagRepository.findByLabel("Project");
        for (Tag projectTag : projectTags) {
            long sampleCount = sampleRepository.countByTags_Id(projectTag.getId());
            projects.add(new ProjectDTO(projectTag.getId(), projectTag.getValue(), sampleCount));
        }

        return projects;
        /*
        // Dummy data, normally pulled from DB
        return List.of(
                new ProjectDTO(uuid1, "Project 1", "This is test project 1. It serves to help test the Projects view.", 10),
                new ProjectDTO(uuid2, "Project 2", "This is test project 2. It serves to help test the Projects view.", 20),
                new ProjectDTO(uuid3, "Project 3", "This is test project 3. It serves to help test the Projects view.", 30)
        );
        */
    }
}
