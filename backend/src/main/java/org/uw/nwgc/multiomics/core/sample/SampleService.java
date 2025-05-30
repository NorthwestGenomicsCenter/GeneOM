package org.uw.nwgc.multiomics.core.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;

    @Autowired
    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    private List<Sample> buildTestSamples() {
        List<Sample> samples = new ArrayList<>();

        // Define sample names
        String[] sampleNames = {
                "UDN178694",
                "UDN687128",
                "BCH_1877-01",
                "BCH_2410-01",
                "UDN633333",
                "UDN121217",
                "BCH_2275-01",
                "UDN052264",
                "UDN212054",
                "BCH_2443-01",
                "UDN215640",
                "BCH_2401-01",
                "BCH_1986-01",
                "UDN374570",
                "BCH_1199-01",
                "BCH_310-01",
                "BCH_0252-01",
                "UDN318336"
        };

        // Generate samples
        for (int i = 0; i < sampleNames.length; i++) {
            Sample sample = new Sample();
            sample.setId(i + 1); // Generate unique ID
            sample.setName("Sample " + (i + 1));
            sample.setExternalLimsId(sampleNames[i]);
            samples.add(sample);
        }

        return samples;
    }

    public List<Sample> fuzzyFindByName(String name, Integer limit) {
        return buildTestSamples().stream()
                .filter(sample -> sample.getExternalLimsId().toLowerCase().contains(name.toLowerCase()))
                .limit(limit)
                .toList();
    }

    public Optional<Sample> getById(Integer id) {
        return sampleRepository.findById(id);
    }
}
