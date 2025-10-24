package org.uw.nwgc.multiomics.core.sample;

import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.resources.dto.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.resources.OmicResourceMapper;
import org.uw.nwgc.multiomics.core.resources.OmicService;
import org.uw.nwgc.multiomics.core.sample.dto.CreateSampleFromExternalDTO;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.tag.TagService;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.core.user.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SampleMapper {

    private final TagService tagService;
    private final OmicResourceMapper omicResourceMapper;
    private final OmicService omicService;

    public SampleMapper(TagService tagService, OmicResourceMapper omicResourceMapper, OmicService omicService) {
        this.tagService = tagService;
        this.omicResourceMapper = omicResourceMapper;
        this.omicService = omicService;
    }

    public SampleDTO toDTO(Sample sample) {
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

    public void updateFromDto(SampleDTO dto, Sample sample, List<Tissue> tissues) {
        sample.setName(dto.getName());
        sample.setIntegrationSource(dto.getIntegrationSource());
        sample.setExternalLimsId(dto.getExternalLimsId());
        sample.setDescription(dto.getDescription());
        sample.setExternalProjectName(dto.getExternalProjectName());

        updateTags(dto, sample);

        if (dto.getOmicResources() != null) {
            for (OmicResourceDTO resourceDTO : dto.getOmicResources()) {
                UUID resourceId = resourceDTO.getOmicResourceId();
                OmicResource resource;
                if (resourceId != null) {
                    resource = omicService.findById(resourceId);
                } else {
                    resource = new OmicResource();
                }

                omicResourceMapper.updateFromDTO(resourceDTO, resource, null);
            }
        }
    }

    private void updateTags(SampleDTO dto, Sample sample) {
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

    public void createFromExternalSample(CreateSampleFromExternalDTO dto, Sample sample) {
        sample.setName(dto.getName());
        sample.setIntegrationSource(dto.getIntegrationSource());
        sample.setExternalLimsId(dto.getExternalLimsId());
        sample.setDescription(dto.getDescription());
        sample.setExternalProjectName(dto.getExternalProjectName());
    }
}
