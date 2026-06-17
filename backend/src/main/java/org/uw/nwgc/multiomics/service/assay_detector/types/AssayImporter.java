package org.uw.nwgc.multiomics.service.assay_detector.types;

import jakarta.security.auth.message.AuthException;

import java.util.UUID;

public interface AssayImporter {
    AssayType getAssayType();
    void importAssay(UUID sampleId, AssayDetectionResult result) throws AuthException;
}
