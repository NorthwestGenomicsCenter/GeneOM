package org.uw.nwgc.multiomics.core.project;

import lombok.Data;

@Data
public class Project {
    private Integer id;
    private String name;
    private String externalProjectId;
}
