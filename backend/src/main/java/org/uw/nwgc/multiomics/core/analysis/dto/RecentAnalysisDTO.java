package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;
import org.uw.nwgc.multiomics.core.analysis.Analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class RecentAnalysisDTO {

    public record RecentAnalysisItem(UUID id, String name) {};

    private List<RecentAnalysisItem> recentAnalysisItems = new ArrayList<>();
    private long totalAnalyses;

    public void add(Analysis analysis){
        recentAnalysisItems.add(new RecentAnalysisItem(analysis.getId(), analysis.getName()));
    }

}


