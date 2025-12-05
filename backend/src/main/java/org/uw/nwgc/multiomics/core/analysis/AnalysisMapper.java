package org.uw.nwgc.multiomics.core.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.analysis.dto.AnalysisEditDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.AnalysisViewDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.CustomReferenceDTO;
import org.uw.nwgc.multiomics.core.cohort.Cohort;
import org.uw.nwgc.multiomics.core.cohort.CohortDTO;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleMapper;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AnalysisMapper {

    @Value("${my.app-base-url:/}")
    private String baseUrl;

    private final SampleMapper sampleMapper;

    public AnalysisMapper(SampleMapper sampleMapper) {
        this.sampleMapper = sampleMapper;
    }

    AnalysisViewDTO toViewDTO(Analysis analysis) {
        AnalysisViewDTO dto = new AnalysisViewDTO();

        // Compute the resources for each sample on the request
        for (Sample sample: analysis.getSamples()) {
            Set<AnalysisViewDTO.AnalysisResource> resources = sample.getOmicResources()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(r -> sampleOmicResource(sample, r))
                    .collect(Collectors.toSet());

            dto.getResources().addAll(resources);
            AnalysisViewDTO.AnalysisSample analysisSample = new AnalysisViewDTO.AnalysisSample(sample.getId(), sample.getName(), resources);
            dto.getSamples().add(analysisSample);
        }

        // Analysis can also have resources from cohort information
        for (Cohort cohort: analysis.getCohorts()) {
            Set<AnalysisViewDTO.AnalysisResource> resources = cohort.getOmicResources()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(r -> cohortOmicResource(cohort, r))
                    .collect(Collectors.toSet());

            dto.getResources().addAll(resources);
        }

        if (analysis.getCustomReference() != null) {
            CustomReferenceDTO customReferenceDTO = new CustomReferenceDTO(analysis.getCustomReference(), baseUrl);
            dto.setCustomReference(customReferenceDTO);
        }

        dto.setLayout(analysis.getLayout());
        dto.setSelectedResources(analysis.getSelectedResources());
        dto.setSavedPositions(analysis.getSavedPositions());

        return dto;
    }

    private static AnalysisViewDTO.AnalysisResource sampleOmicResource(Sample sample, OmicResource omicResource) {
        String path = FileUtil.resolvePath(omicResource.getPath()).getFileName().toString();
        String name = path;
        if (omicResource.getDisplayName() != null) {
            name = omicResource.getDisplayName();
        }

        return new AnalysisViewDTO.AnalysisResource(
                omicResource.getId(),
                name,
                omicResource.getDescription(),
                sample.getId(),
                null,
                sample.getName(),
                omicResource.getVisualizers(),
                path
        );
    }

    private static AnalysisViewDTO.AnalysisResource cohortOmicResource(Cohort cohort, OmicResource omicResource) {
        String path = FileUtil.resolvePath(omicResource.getPath()).getFileName().toString();
        String name = path;
        if (omicResource.getDisplayName() != null) {
            name = omicResource.getDisplayName();
        }

        return new AnalysisViewDTO.AnalysisResource(
                omicResource.getId(),
                name,
                omicResource.getDescription(),
                null,
                cohort.getId(),
                cohort.getName(),
                omicResource.getVisualizers(),
                path
        );
    }

    public AnalysisEditDTO toEditDTO(Analysis analysis) {
        AnalysisEditDTO dto = new AnalysisEditDTO();
        dto.setName(analysis.getName());
        dto.setDescription(analysis.getDescription());
        if (analysis.getLastModified() != null) {
            dto.setLastModified(analysis.getLastModified().toString());
        }
        if (analysis.getReferenceGenome() != null) {
            dto.setReferenceGenome(analysis.getReferenceGenome().name());
        }
        if (analysis.getCustomReference() != null) {
            CustomReference ref = analysis.getCustomReference();
            CustomReferenceDTO customReference = new CustomReferenceDTO(ref, baseUrl);
             dto.setCustomReferenceGenome(customReference);
        }

        dto.setSamples(analysis.getSamples()
                .stream()
                .map(sampleMapper::toDTO)
                .collect(Collectors.toSet()));

        dto.setCohorts(analysis.getCohorts()
            .stream()
            .map(CohortDTO::new)
            .collect(Collectors.toSet()));

        return dto;
    }
}
