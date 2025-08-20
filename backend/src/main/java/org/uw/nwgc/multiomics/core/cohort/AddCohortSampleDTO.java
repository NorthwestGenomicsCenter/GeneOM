package org.uw.nwgc.multiomics.core.cohort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCohortSampleDTO {
    private UUID cohortId;
    private List<SampleDTO> samples;
}
