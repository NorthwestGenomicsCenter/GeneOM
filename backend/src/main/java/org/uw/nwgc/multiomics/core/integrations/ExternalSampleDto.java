package org.uw.nwgc.multiomics.core.integrations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalSampleDto {
    private UUID multiomicsSampleId;
    private String integrationSource;
    private String sampleId;
    private String sampleName;
    private String sampleDescription;
    private String projectName;
    private List<SampleFileDTO> sampleFiles;
}
