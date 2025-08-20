package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisViewDTO {
    private String layout;

    public record AnalysisResource(
            UUID resourceId,
            String resourceName,
            UUID sampleId,
            String sampleName,
            List<String> technologiesViewers
    ) {};
    public record AnalysisSample(UUID sampleId, String sampleName, Set<AnalysisResource> resources) {};
    private Set<AnalysisSample> samples = new HashSet<>();

    private Set<AnalysisResource> resources = new HashSet<>();

    private CustomReferenceDTO customReference;
}
