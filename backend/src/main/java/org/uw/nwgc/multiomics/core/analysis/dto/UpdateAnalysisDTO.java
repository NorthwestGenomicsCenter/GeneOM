package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;

@Data
public class UpdateAnalysisDTO {
    private String name;
    private String description;
    private String referenceGenome;
}
