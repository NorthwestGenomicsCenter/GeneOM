package org.uw.nwgc.multiomics.core.tag;

import java.util.UUID;

public interface ManagedTagWithSampleCount {
    UUID getId();
    String getValue();
    String getDescription();
    long getSampleCount();
}
