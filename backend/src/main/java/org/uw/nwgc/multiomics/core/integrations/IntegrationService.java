package org.uw.nwgc.multiomics.core.integrations;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleRepository;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IntegrationService {
    private final List<Integration> integrations;
    private final SampleService sampleService;

    public IntegrationService(
            List<Integration> integrations,
            SampleService sampleService
    ) {
        this.integrations = integrations;
        this.sampleService = sampleService;
    }

    public List<SampleFileDTO> queryIntegration(String queryString) {
        List<SampleFileDTO> sampleFileDtos = new ArrayList<>();

        for (Integration integration : integrations) {
            sampleFileDtos.addAll(integration.querySampleFiles(queryString));
        }

        return sampleFileDtos;
    }

    public List<ExternalSampleDto> querySampleIntegration(String queryString) {
        List<ExternalSampleDto> externalSampleDTOs = new ArrayList<>();

        for (Integration integration : integrations) {
            List<ExternalSampleDto> dtos = integration.queryExternalSamples(queryString);
            dtos.forEach(dto -> {
                dto.setIntegrationSource(integration.integrationName());
                Optional<Sample> existingSample = sampleService.findExistingIntegrationSample(dto.getSampleId(), integration.integrationName());
                existingSample.ifPresent(sample -> dto.setMultiomicsSampleId(sample.getId()));
            });
            externalSampleDTOs.addAll(dtos);
        }

        return externalSampleDTOs;
    }
}
