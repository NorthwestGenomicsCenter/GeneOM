package org.uw.nwgc.multiomics.core.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.resources.OmicController;
import org.uw.nwgc.multiomics.core.resources.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.user.UserDTO;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SampleDTO {
    private UUID id;
    private String name;
    private String integrationSource;
    private String externalLimsId;
    private String externalProjectName;
    private String description;
    private Set<Tag.SampleTagDTO> tags;
    private List<UserDTO> users;
    private List<OmicResourceDTO> omicResources;

    public SampleDTO(Sample sample) {
        this.id = sample.getId();
        this.name = sample.getName();
        this.integrationSource = sample.getIntegrationSource();
        this.externalLimsId = sample.getExternalLimsId();
        this.externalProjectName = sample.getExternalProjectName();
        this.description = sample.getDescription();
        this.tags = sample.getTags().stream().map(tag -> {
                if (tag != null) {
                    return new Tag.SampleTagDTO(tag.getId(), tag.getLabel(), tag.getValue());
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        this.users = sample.getUsers().stream().map(UserDTO::new).collect(Collectors.toList());
        this.omicResources = sample.getOmicResources().stream()
                .filter(Objects::nonNull)
                .map(OmicResourceDTO::new)
                .toList();
    }
}
