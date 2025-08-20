package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnalysisDTO {
    private String name;
    private String description;
}
