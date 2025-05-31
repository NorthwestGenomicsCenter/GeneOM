package org.uw.nwgc.multiomics.core.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SampleDTO {
    private UUID id;
    private String name;
    private String externalLimsId;
    private String externalProjectName;
    private String description;
}
