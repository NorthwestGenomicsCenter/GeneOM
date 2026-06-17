package org.uw.nwgc.multiomics.service.assay_detector;

import org.uw.nwgc.multiomics.service.assay_detector.types.AssayDetectionResult;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayDetector;
import org.uw.nwgc.multiomics.service.assay_detector.types.DetectedFile;
import org.uw.nwgc.multiomics.service.assay_detector.types.FilePattern;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractAssayDetector implements AssayDetector {

    @Override
    public AssayDetectionResult detect(List<String> files, Path directory) {
        List<DetectedFile> detectedFiles = new ArrayList<>();
        List<FilePattern> missingPatterns = new ArrayList<>();

        for (FilePattern pattern : getPatterns()) {
            Optional<DetectedFile> match = findMatch(files, pattern, directory);
            if (match.isPresent()) {
                detectedFiles.add(match.get());
            } else {
                missingPatterns.add(pattern);
            }
        }

        Optional<String> samplePrefix = consensusPrefix(detectedFiles);
        double confidence = calculateConfidence(detectedFiles, missingPatterns);

        return new AssayDetectionResult(
                getAssayType(),
                confidence,
                Collections.unmodifiableList(detectedFiles),
                samplePrefix,
                directory,
                Collections.unmodifiableList(missingPatterns)
        );
    }

    private Optional<DetectedFile> findMatch(List<String> files, FilePattern pattern, Path directory) {
        for (String filename : files) {
            if (filename.equals(pattern.suffix()) || filename.endsWith("." + pattern.suffix())) {
                String prefix = filename.endsWith("." + pattern.suffix())
                        ? filename.substring(0, filename.length() - pattern.suffix().length() - 1)
                        : "";
                return Optional.of(new DetectedFile(pattern, filename, prefix, directory.resolve(filename)));
            }
        }
        return Optional.empty();
    }

    private Optional<String> consensusPrefix(List<DetectedFile> detectedFiles) {
        return detectedFiles.stream()
                .map(DetectedFile::samplePrefix)
                .filter(p -> !p.isEmpty())
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    private double calculateConfidence(List<DetectedFile> detectedFiles, List<FilePattern> missingPatterns) {
        if (missingPatterns.stream().anyMatch(FilePattern::required)) return 0.0;

        int totalWeight = getPatterns().stream().mapToInt(FilePattern::weight).sum();
        int matchedWeight = detectedFiles.stream().mapToInt(f -> f.pattern().weight()).sum();

        return totalWeight > 0 ? (double) matchedWeight / totalWeight : 0.0;
    }
}
