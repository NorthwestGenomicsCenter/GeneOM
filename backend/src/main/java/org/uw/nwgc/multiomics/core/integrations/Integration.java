package org.uw.nwgc.multiomics.core.integrations;

import java.util.List;

public interface Integration {

    List<ExternalSampleDto> queryExternalSamples(String queryString);
    List<SampleFileDto> querySampleFiles(String queryString);
}
