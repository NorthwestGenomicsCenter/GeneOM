package org.uw.nwgc.multiomics.core.sample.dto;

import java.util.List;
import java.util.UUID;

public record AddExternalSampleResourcesDTO(
        UUID sampleId,
        List<String> filePaths)
{}
