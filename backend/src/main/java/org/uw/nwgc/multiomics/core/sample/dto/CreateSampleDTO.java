package org.uw.nwgc.multiomics.core.sample.dto;

import lombok.Data;

@Data
public class CreateSampleDTO {
    private String name;
    private String projectName;
    private String description;
}