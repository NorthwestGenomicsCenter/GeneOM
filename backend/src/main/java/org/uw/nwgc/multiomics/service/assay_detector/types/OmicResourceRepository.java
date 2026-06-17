package org.uw.nwgc.multiomics.service.assay_detector.types;

import java.nio.file.Path;

/**
 * Implement this with your ORDataManager / Hibernate session calls and annotate
 * the implementation with @Repository.
 */
public interface OmicResourceRepository {
    void createResource(long sampleId, AssayType assayType, ResourceType resourceType,
                        String filename, Path fullPath, String description);

    void createSetting(long sampleId, String key, String value);
}
