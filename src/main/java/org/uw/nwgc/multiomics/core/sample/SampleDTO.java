package org.uw.nwgc.multiomics.core.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SampleDTO {
    public Integer id;
    public String name;
    public String externalLimsId;
    public Integer projectId;
}
