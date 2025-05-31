package org.uw.nwgc.multiomics.core.cohort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CohortDTO {
    private UUID id;
    private String name;
    private String description;
    private Integer count;
}
