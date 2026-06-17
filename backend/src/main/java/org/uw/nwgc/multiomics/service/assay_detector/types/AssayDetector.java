package org.uw.nwgc.multiomics.service.assay_detector.types;

import java.nio.file.Path;
import java.util.List;

public interface AssayDetector {
    AssayType getAssayType();
    List<FilePattern> getPatterns();
    AssayDetectionResult detect(List<String> files, Path directory);
}
