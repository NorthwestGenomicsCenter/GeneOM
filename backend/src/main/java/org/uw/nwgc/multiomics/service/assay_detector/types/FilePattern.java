package org.uw.nwgc.multiomics.service.assay_detector.types;

public record FilePattern(
        String suffix,
        boolean required,
        int weight,
        ResourceType resourceType,
        String description
) {}
