package org.uw.nwgc.multiomics.core.sample.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateSampleFromExternalDTO {
    private UUID sampleId;
    private String name;
    private String integrationSource;
    private String externalLimsId;
    private String externalProjectName;
    private String description;
    private List<String> filePaths;
}
