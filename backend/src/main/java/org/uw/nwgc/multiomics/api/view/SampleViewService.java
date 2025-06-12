package org.uw.nwgc.multiomics.api.view;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.api.ViewName;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SampleViewService {

    public List<SampleViewDTO> getAvailableTechnologyViewPairsForSample(String sampleId) {
        // TODO: Implement logic to determine available technologies and views.

        // Dummy data, normally pulled from DB
        return List.of(
                new SampleViewDTO("RNA-seq", ViewName.DIFFERENTIAL_EXPRESSION.getApiValue()),
                new SampleViewDTO("RNA-seq", ViewName.DIFFERENTIAL_PEAK.getApiValue()),
                new SampleViewDTO("ATAC-seq", ViewName.IGV.getApiValue())
        );
    }

    public Map<String, List<String>> getAvailableViewsByTechnologyForSample(String sampleId) {
        // TODO: Replace with actual logic using sampleId.
        Map<String, List<ViewName>> mock = Map.of(
                "rna-seq", List.of(ViewName.DIFFERENTIAL_EXPRESSION, ViewName.DIFFERENTIAL_PEAK),
                "atac-seq", List.of(ViewName.IGV)
        );

        // Convert ViewName enums to string view names
        return mock.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(ViewName::getApiValue)
                                .toList()
                ));
    }

}
