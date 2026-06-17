package org.uw.nwgc.multiomics.core.cohort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uw.nwgc.multiomics.core.resources.dto.OmicResourceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CohortDTO {
    private UUID id;
    private String name;
    private String description;
    private Integer count;
    private List<OmicResourceDTO> omicResources = new ArrayList<>();

    public CohortDTO(Cohort cohort) {
        this.id = cohort.getId();
        this.name = cohort.getName();
        this.description = cohort.getDescription();

        for (var resource : cohort.getOmicResources()) {
            omicResources.add(new OmicResourceDTO(resource));
        }
    }
}
