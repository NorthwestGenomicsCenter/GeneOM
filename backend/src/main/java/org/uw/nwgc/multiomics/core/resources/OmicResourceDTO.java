package org.uw.nwgc.multiomics.core.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OmicResourceDTO {
    private UUID omicResourceId;
    private UUID sampleid;
    private String path;
    private Boolean igvTrack;
    private List<String> visualizers;

    public OmicResourceDTO(OmicResource omicResource) {
        this.omicResourceId = omicResource.getId();
        this.sampleid = omicResource.getSample().getId();
        this.path = omicResource.getPath();
        this.visualizers = omicResource.getVisualizers();
        this.igvTrack = omicResource.getIgvTrack();
    }

}
