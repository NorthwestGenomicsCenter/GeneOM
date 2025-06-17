package org.uw.nwgc.multiomics.core.resources;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Data
public class AddOmicsResourceDTO {
    private UUID sampleId;
    private String path;
    private List<String> visualizers;
    private Boolean igvTrack;
}
