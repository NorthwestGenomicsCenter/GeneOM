package org.uw.nwgc.multiomics.core.snapshot.dto;

import java.util.UUID;

public record SaveAnalysisPositionDTO(
        UUID analysisId,
        String chromosome,
        String start,
        String end,
        String notes
) {}
