package org.uw.nwgc.multiomics.core.integrations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleFileDto {
    private String sampleId;
    private String path;
}
