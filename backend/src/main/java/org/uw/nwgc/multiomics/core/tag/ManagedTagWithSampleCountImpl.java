package org.uw.nwgc.multiomics.core.tag;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ManagedTagWithSampleCountImpl implements ManagedTagWithSampleCount {
    private final UUID id;
    private final String value;
    private final String description;
    private final long sampleCount;

    public ManagedTagWithSampleCountImpl(UUID id, String value, String description, long sampleCount) {
        this.id = id;
        this.value = value;
        this.description = description;
        this.sampleCount = sampleCount;
    }
}

