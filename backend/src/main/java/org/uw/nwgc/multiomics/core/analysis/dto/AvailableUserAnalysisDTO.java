package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;
import org.uw.nwgc.multiomics.core.analysis.Analysis;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
public class AvailableUserAnalysisDTO {
    private UUID id;
    private String name;
    private LocalDateTime lastAccessedTime;

    public AvailableUserAnalysisDTO(Analysis analysis) {
        this.id = analysis.getId();
        this.name = analysis.getName();
        this.lastAccessedTime = analysis.getLastModified();
    }

    public static Collection<AvailableUserAnalysisDTO> getAvailableUserAnalysisDTO(Collection<Analysis> analysis){
        return analysis.stream()
                .map(AvailableUserAnalysisDTO::new)
                .toList();
    }
}
