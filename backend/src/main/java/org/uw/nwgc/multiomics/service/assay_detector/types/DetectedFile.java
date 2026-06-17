package org.uw.nwgc.multiomics.service.assay_detector.types;

import java.nio.file.Path;

public record DetectedFile(
        FilePattern pattern,
        String filename,
        String samplePrefix,
        Path fullPath
) {}
