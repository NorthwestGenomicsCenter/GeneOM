package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.snapshot.AnalysisPositionSnapshot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisViewDTO {
    private String layout;
    private String selectedResources;

    public record AnalysisResource(
            UUID resourceId,
            String resourceName,
            String description,
            UUID sampleId,
            UUID cohortId,
            String name,
            List<String> technologiesViewers,
            String path
    ) {};

    public record AnalysisSample(UUID sampleId, String name, Set<AnalysisResource> resources) {};

    private CustomReferenceDTO customReference;
    private Set<AnalysisResource> resources = new HashSet<>();
    private Set<AnalysisSample> samples = new HashSet<>();
    private List<AnalysisPositionSnapshot> savedPositions;
}
