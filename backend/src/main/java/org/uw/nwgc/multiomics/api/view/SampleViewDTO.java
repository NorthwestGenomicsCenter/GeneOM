package org.uw.nwgc.multiomics.api.view;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SampleViewDTO {
    private String technology;
    private String viewName; // from enum
    private UUID omicResourceId;

    public SampleViewDTO(String technology, String viewName) {
        this.technology = technology;
        this.viewName = viewName;
    }

    public SampleViewDTO(String technology, String viewName, UUID omicResourceId) {
        this.technology = technology;
        this.viewName = viewName;
        this.omicResourceId = omicResourceId;
    }
}
