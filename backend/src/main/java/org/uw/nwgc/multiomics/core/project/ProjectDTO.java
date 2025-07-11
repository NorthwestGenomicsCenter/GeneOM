package org.uw.nwgc.multiomics.core.project;

import java.util.UUID;

public class ProjectDTO {
    private UUID id;
    private String name;
    private String description;
    private long sampleCount;

    public ProjectDTO(UUID id, String name, long sampleCount) {
        this.id = id;
        this.name = name;
        this.description = "This is a placeholder description while this feature is being implemented.";
        this.sampleCount = sampleCount;
    }

    public ProjectDTO(UUID id, String name, String description, long sampleCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sampleCount = sampleCount;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public long getSampleCount() { return sampleCount; }
}
