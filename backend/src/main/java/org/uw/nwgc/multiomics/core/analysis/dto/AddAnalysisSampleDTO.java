package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAnalysisSampleDTO {
    private UUID analysisId;
    private List<SampleDTO> samples;
}
