package org.uw.nwgc.multiomics.core.snapshot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uw.nwgc.multiomics.core.snapshot.AnalysisLayoutSnapshot;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SaveAnalysisLayoutDTO {
    public String layout;
    public Map<UUID, List<AnalysisLayoutSnapshot.ComponentResources>> selectedResources;
}
