package org.uw.nwgc.multiomics.core.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;
import org.uw.nwgc.multiomics.core.sample.SampleRepository;
import org.uw.nwgc.multiomics.core.tag.ManagedTag;
import org.uw.nwgc.multiomics.core.tag.ManagedTagRepository;
import org.uw.nwgc.multiomics.core.tag.ManagedTagWithSampleCount;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ManagedTagRepository managedTagRepository;

    public ProjectService(ManagedTagRepository managedTagRepository) {
        this.managedTagRepository = managedTagRepository;
    }

    public Page<ProjectDTO> getAllProjects(Pageable pageable) {
            Page<ManagedTagWithSampleCount> tagPage =
                    managedTagRepository.findWithSampleCountsByType(ManageTagType.PROJECT, null, pageable);

            return tagPage.map(tag -> new ProjectDTO(
                    tag.getId(),
                    tag.getValue(),
                    tag.getDescription(),
                    tag.getSampleCount()
            ));
    }

    public List<String> getAllNames() {
        return managedTagRepository.findAllValues(ManageTagType.PROJECT);
    }

    public boolean nameExists(String name) {
        return managedTagRepository.existsByTypeAndValue(ManageTagType.PROJECT, name);
    }
}
