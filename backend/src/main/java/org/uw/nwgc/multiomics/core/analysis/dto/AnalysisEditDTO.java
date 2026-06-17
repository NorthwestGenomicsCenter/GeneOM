package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;
import org.uw.nwgc.multiomics.core.cohort.CohortDTO;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;
import org.uw.nwgc.multiomics.core.user.UserDTO;

import java.util.List;
import java.util.Set;

@Data
public class AnalysisEditDTO {
    String name;
    String description;
    String lastModified;
    String referenceGenome;
    CustomReferenceDTO customReferenceGenome;
    Set<SampleDTO> samples;
    Set<CohortDTO> cohorts;
    List<UserDTO> users;
}
