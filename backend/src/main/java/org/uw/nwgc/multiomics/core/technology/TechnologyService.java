package org.uw.nwgc.multiomics.core.technology;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleRepository;
import org.uw.nwgc.multiomics.core.tag.ManagedTag;
import org.uw.nwgc.multiomics.core.tag.ManagedTagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TechnologyService {

    private ManagedTagRepository managedTagRepository;
    private SampleRepository sampleRepository;

    public TechnologyService(ManagedTagRepository managedTagRepository, SampleRepository sampleRepository) {
        this.managedTagRepository = managedTagRepository;
        this.sampleRepository = sampleRepository;
    }

    public List<TechnologyDTO> getAllTechnologies() {
        List<TechnologyDTO> technologies = new ArrayList<>();

        List<ManagedTag> technologyTags = managedTagRepository.findByType(ManageTagType.TECHNOLOGY);
        for (ManagedTag technologyTag : technologyTags) {
            long sampleCount = sampleRepository.countByTags_Id(technologyTag.getId());
            technologies.add(new TechnologyDTO(technologyTag.getId(), technologyTag.getValue(), sampleCount));
        }

        return technologies;
    }

    public List<TechnologyDTO> getTechnologiesForProject(UUID projectId) {
        // Get all samples that are tagged with the given project
        List<Sample> samplesWithProject = sampleRepository.findByTags_Id(projectId);

        // Create a frequency map of technology tag IDs
        Map<UUID, Long> technologyCountMap = samplesWithProject.stream()
                .flatMap(sample -> sample.getManagedTags().stream()
                        .filter(tag -> tag.getType().equals(ManageTagType.TECHNOLOGY)))
                .collect(Collectors.groupingBy(ManagedTag::getId, Collectors.counting()));

        // Map distinct technology tags into DTOs with their sample counts
        return samplesWithProject.stream()
                .flatMap(sample -> sample.getManagedTags().stream())
                .filter(tag -> tag.getType().equals(ManageTagType.TECHNOLOGY))
                .distinct()
                .map(tag -> new TechnologyDTO(
                        tag.getId(),
                        tag.getValue(),
                        tag.getDescription(),
                        technologyCountMap.getOrDefault(tag.getId(), 0L)
                ))
                .toList();

    }

    public List<String> getAllNames() {
        return managedTagRepository.findAllValues(ManageTagType.TECHNOLOGY);
    }

    public boolean nameExists(String name) {
        return managedTagRepository.existsByTypeAndValue(ManageTagType.TECHNOLOGY, name);
    }
}
