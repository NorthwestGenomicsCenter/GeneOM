package org.uw.nwgc.multiomics.core.finder;

public record VueFinderResponseDTO(
        String directoryPath,
        String assayType,
        Object payload
) {
}
