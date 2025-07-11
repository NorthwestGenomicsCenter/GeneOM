package org.uw.nwgc.multiomics.core.sample;

import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.resources.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.core.user.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SampleMapper {

    public static SampleDTO toDTO(Sample sample) {
        SampleDTO dto = new SampleDTO();
        dto.setId(sample.getId());
        dto.setName(sample.getName());
        dto.setIntegrationSource(sample.getIntegrationSource());
        dto.setExternalLimsId(sample.getExternalLimsId());
        dto.setExternalProjectName(sample.getExternalProjectName());
        dto.setDescription(sample.getDescription());
        dto.setUsers(sample.getUsers().stream().map(UserDTO::new).collect(Collectors.toList()));

        dto.setTags(sample.getTags().stream().map(tag -> {
                    if (tag != null) {
                        return new Tag.SampleTagDTO(tag.getId(), tag.getLabel(), tag.getValue());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        dto.setOmicResources(sample.getOmicResources().stream()
                .filter(Objects::nonNull)
                .map(OmicResourceDTO::new)
                .toList());

        return dto;
    }

    public static void updateFromDto(SampleDTO dto, Sample sample, List<Tissue> tissues) {
        sample.setName(dto.getName());
        sample.setIntegrationSource(dto.getIntegrationSource());
        sample.setExternalLimsId(dto.getExternalLimsId());
        sample.setDescription(dto.getDescription());
        sample.setExternalProjectName(dto.getExternalProjectName());

        updateTags(dto, sample);
        updateOmicResources(dto, sample, tissues);
    }

    private static void updateTags(SampleDTO dto, Sample sample) {
        Map<UUID, Tag> existingTags = sample.getTags()
                .stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (var tagDTO : dto.getTags()) {
                Tag tag;
                if (tagDTO.id() == null) {
                    tag = new Tag();
                } else {
                    tag = existingTags.computeIfAbsent(tagDTO.id(), k -> new Tag());
                }

                tag.updateTag(tagDTO);
                sample.getTags().add(tag);
            }
        }
    }

    private static void updateOmicResources(SampleDTO dto, Sample sample, List<Tissue> tissues) {
        Map<UUID, OmicResource> existingOmicResources = sample.getOmicResources()
                .stream()
                .collect(Collectors.toMap(OmicResource::getId, Function.identity()));

        if (dto.getOmicResources() != null && !dto.getOmicResources().isEmpty()) {
            for (OmicResourceDTO omicResourceDTO : dto.getOmicResources()) {
                OmicResource omicResource;
                if (omicResourceDTO.getOmicResourceId() == null) {
                    omicResource = new OmicResource();
                } else {
                    omicResource = existingOmicResources.computeIfAbsent(omicResourceDTO.getOmicResourceId(), k -> new OmicResource());
                }

                omicResource.update(omicResourceDTO, tissues);
                sample.getOmicResources().add(omicResource);
            }
        }
    }
}
