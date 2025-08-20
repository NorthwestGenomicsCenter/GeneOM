package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;

import java.util.Set;

@Data
public class AnalysisEditDTO {
    String name;
    String description;
    String lastModified;
    String referenceGenome;
    CustomReferenceDTO customReferenceGenome;
    Set<SampleDTO> samples;
}
