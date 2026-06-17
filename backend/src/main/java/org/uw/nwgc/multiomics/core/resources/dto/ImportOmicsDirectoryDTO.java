package org.uw.nwgc.multiomics.core.resources.dto;

import java.util.UUID;

public record ImportOmicsDirectoryDTO(
        UUID sampleId,
        String directoryPath,
        String assayType
) {}
