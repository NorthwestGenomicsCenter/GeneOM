package org.uw.nwgc.multiomics.core.technology;

import java.util.UUID;

public class TechnologyDTO {
    private UUID id;
    private String name;
    private String description;
    private int sampleCount;

    public TechnologyDTO(UUID id, String name, String description, int sampleCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sampleCount = sampleCount;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public int getSampleCount() { return sampleCount; }
}
