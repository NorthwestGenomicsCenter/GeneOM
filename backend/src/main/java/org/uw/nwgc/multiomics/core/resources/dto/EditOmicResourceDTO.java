package org.uw.nwgc.multiomics.core.resources.dto;

import java.util.List;

public record EditOmicResourceDTO(
   String description,
   String displayName,
   List<String> visualizationComponents
) {}