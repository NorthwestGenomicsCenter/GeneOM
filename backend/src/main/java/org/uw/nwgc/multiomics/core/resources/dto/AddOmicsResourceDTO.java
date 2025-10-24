package org.uw.nwgc.multiomics.core.resources.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddOmicsResourceDTO {
    private UUID sampleId;
    private String path;
    private List<String> visualizers;
    private Boolean igvTrack;
}
