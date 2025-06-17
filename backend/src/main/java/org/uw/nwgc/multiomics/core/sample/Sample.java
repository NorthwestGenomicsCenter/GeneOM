package org.uw.nwgc.multiomics.core.sample;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.resources.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sample {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;
    private String name;
    private String externalLimsId;
    private String integrationSource;
    private String description;
    private String externalProjectName;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<OmicResource> omicResources = new HashSet<>();

    @ManyToMany
    private Set<User> users = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tag> tags = new HashSet<>();

    protected Sample update(SampleDTO dto) {
        this.name = dto.getName();
        this.integrationSource = dto.getIntegrationSource();
        this.externalLimsId = dto.getExternalLimsId();
        this.description = dto.getDescription();
        this.externalProjectName = dto.getExternalProjectName();

        updateTags(dto);
        updateOmicResources(dto);

        return this;
    }

    private void updateOmicResources(SampleDTO dto) {
        Map<UUID, OmicResource> existingOmicResources = omicResources
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

                omicResource.update(omicResourceDTO);
                omicResources.add(omicResource);
            }
        }
    }

    private void updateTags(SampleDTO dto) {
        Map<UUID, Tag> existingTags = tags
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
                tags.add(tag);
            }
        }
    }
}