package org.uw.nwgc.multiomics.service.assay_detector.importers;

import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.resources.OmicService;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;
import org.uw.nwgc.multiomics.service.assay_detector.types.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("chia_tac_directory_importer")
public class ChiaTacImporter implements AssayImporter {

//    private final OmicResourceRepository repo;
//
//    public ChiaTacImporter(OmicResourceRepository repo) {
//        this.repo = repo;
//    }
    private final OmicService omicService;
    private final SampleService sampleService;

    public ChiaTacImporter(OmicService omicService, SampleService sampleService) {
        this.omicService = omicService;
        this.sampleService = sampleService;
    }

    @Override
    public AssayType getAssayType() {
        return AssayType.CHIATAC;
    }

    @Override
    @Transactional
    public void importAssay(UUID sampleId, AssayDetectionResult result) throws AuthException {
        if (result.confidence() == 0.0) {
            String missing = result.missingPatterns().stream()
                    .filter(FilePattern::required)
                    .map(FilePattern::suffix)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Cannot import ChIATAC for sample " + sampleId +
                    ": confidence is 0. Missing required patterns: " + missing);
        }

        Sample sample = sampleService.getById(sampleId).orElseThrow(
                () -> new IllegalArgumentException("Cannot import ChIATAC for sample " + sampleId)
        );

        Map<String, OmicResource> sampleOmicResources = sample.resourceFilenameMap();

        for (DetectedFile f : result.detectedFiles()) {
            Path detectedPath = f.fullPath();

            OmicResource diskResource;
            if (sampleOmicResources.containsKey(detectedPath.getFileName().toString())) {
                diskResource = sampleOmicResources.get(detectedPath.getFileName().toString());
                sample.getOmicResources().add(diskResource);
            } else {
                diskResource = new OmicResource(sample, detectedPath.toString());
            }

            diskResource.updateFromDetectedFile(f);
        }

        sampleService.save(sample);
    }
}
