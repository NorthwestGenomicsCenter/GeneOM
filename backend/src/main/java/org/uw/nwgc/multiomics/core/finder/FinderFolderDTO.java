package org.uw.nwgc.multiomics.core.finder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FinderFolderDTO {
    private String adapter; // = "sharing";
    private String basename; // = "test";
    private String path; // =  "sharing://test";
}
