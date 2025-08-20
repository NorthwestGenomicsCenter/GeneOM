package org.uw.nwgc.multiomics.core.project;

import org.uw.nwgc.multiomics.core.enums.ManageTagType;
import org.uw.nwgc.multiomics.core.tag.ManagedTag;

public class ProjectMapper {
    public ManagedTag toEntity(ProjectDTO dto) {
        return new ManagedTag(ManageTagType.PROJECT, dto.getName(), dto.getDescription());
    }

    public ProjectDTO toDTO(ManagedTag entity) {
        if (entity == null) return null;
        if (!ManageTagType.PROJECT.equals(entity.getType())) return null;
        return new ProjectDTO(entity.getId(), entity.getValue(), entity.getDescription(), 1);
    }
}
