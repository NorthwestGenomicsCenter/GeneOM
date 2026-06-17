package org.uw.nwgc.multiomics.core.cohort.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCohortResourceDTO {
    private UUID cohortId;
    private List<String> filePaths;
}