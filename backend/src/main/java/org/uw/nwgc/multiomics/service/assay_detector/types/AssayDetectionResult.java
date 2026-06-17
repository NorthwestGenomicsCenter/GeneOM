package org.uw.nwgc.multiomics.service.assay_detector.types;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public record AssayDetectionResult(
        AssayType assayType,
        double confidence,
        List<DetectedFile> detectedFiles,
        Optional<String> samplePrefix,
        Path directory,
        List<FilePattern> missingPatterns
) {}
