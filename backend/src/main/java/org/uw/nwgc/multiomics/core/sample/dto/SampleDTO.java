package org.uw.nwgc.multiomics.core.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.resources.dto.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.user.UserDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public record SampleAnalysisDTO(UUID id, String name, String description) {};
    private List<SampleAnalysisDTO> sampleAnalysis;

}
