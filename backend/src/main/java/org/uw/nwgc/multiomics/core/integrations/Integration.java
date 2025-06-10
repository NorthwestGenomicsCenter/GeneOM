package org.uw.nwgc.multiomics.core.integrations;

import java.util.List;

public interface Integration {

    /**
     * Returns the unique name of the external integration source
     * that populated this sample.
     *
     * <p>This identifier can be used together with the sample’s
     * external LIMS ID to look up the original data in the source system.</p>
     *
     * @return the integration’s unique name
     */
    String integrationName();
    List<ExternalSampleDto> queryExternalSamples(String queryString);
    List<SampleFileDTO> querySampleFiles(String queryString);
}
