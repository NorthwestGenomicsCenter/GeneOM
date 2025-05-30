package org.uw.nwgc.multiomics.core.integrations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalSampleDto {
    private String sampleId;
    private String sampleName;
    private String sampleDescription;
    private String projectName;
}
