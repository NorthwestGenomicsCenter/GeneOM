package org.uw.nwgc.multiomics.service.assay_detector;

import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayDetectionResult;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayDetector;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayImporter;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AssayDetectionService {

    public static final double CONFIDENCE_THRESHOLD = 0.3;

    private final List<AssayDetector> detectors;
    private final Map<AssayType, AssayImporter> importers;

    /**
     * Spring injects every @Component that implements AssayDetector or AssayImporter
     * automatically — no manual registration needed. To add a new assay, just create
     * the detector and importer as @Component classes and Spring picks them up.
     */
    public AssayDetectionService(List<AssayDetector> detectors, List<AssayImporter> importers) {
        this.detectors = detectors;
        this.importers = importers.stream()
                .collect(Collectors.toMap(AssayImporter::getAssayType, Function.identity()));
    }

    /**
     * Scan a directory against all known assay patterns.
     * Returns all results above the confidence threshold, sorted best-match first.
     * Call this when the user navigates to a new directory.
     */
    public List<AssayDetectionResult> investigateDirectory(Path directory) throws IOException {
        List<String> files = Files.list(directory)
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        return detectors.stream()
                .map(detector -> detector.detect(files, directory))
                .filter(result -> result.confidence() >= CONFIDENCE_THRESHOLD)
                .sorted(Comparator.comparingDouble(AssayDetectionResult::confidence).reversed())
                .collect(Collectors.toList());
    }

    public AssayDetectionResult initializeDirectory(Path directory, AssayType type) throws IOException {
        List<String> files = Files.list(directory)
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        return detectors.stream()
                .filter(detector -> type.equals(detector.getAssayType()))
                .map(detector -> detector.detect(files, directory))
                .findFirst()
                .orElse(null);
    }

    /**
     * Import a detected assay for a sample — creates all DB records and settings.
     * Call this after the user confirms which assay to import.
     */
    public void importAssay(UUID sampleId, AssayDetectionResult result) throws AuthException {
        AssayImporter importer = importers.get(result.assayType());
        if (importer == null) {
            throw new IllegalArgumentException("No importer registered for assay type: " + result.assayType());
        }
        importer.importAssay(sampleId, result);
    }

    /**
     * Convenience: investigate then immediately import the best match above minConfidence.
     * Returns the imported result, or empty if nothing matched well enough.
     */
    public Optional<AssayDetectionResult> detectAndImport(UUID sampleId, Path directory, double minConfidence)
            throws IOException {
        return investigateDirectory(directory).stream()
                .filter(r -> r.confidence() >= minConfidence)
                .findFirst()
                .map(best -> {
                    try {
                        importAssay(sampleId, best);
                    } catch (AuthException e) {
                        throw new RuntimeException(e);
                    }
                    return best;
                });
    }
}
