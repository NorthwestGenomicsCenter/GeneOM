package org.uw.nwgc.multiomics.core.integrations;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegrationService {

    private final List<Integration> integrations;

    public IntegrationService(List<Integration> integrations) {
        this.integrations = integrations;
    }

    public List<SampleFileDto> queryIntegration(String queryString) {
        List<SampleFileDto> sampleFileDtos = new ArrayList<>();

        for (Integration integration : integrations) {
            sampleFileDtos.addAll(integration.querySampleFiles(queryString));
        }

        return sampleFileDtos;
    }

    public List<ExternalSampleDto> querySampleIntegration(String queryString) {
        List<ExternalSampleDto> sampleFileDtos = new ArrayList<>();

        for (Integration integration : integrations) {
            sampleFileDtos.addAll(integration.queryExternalSamples(queryString));
        }

        return sampleFileDtos;
    }
}
