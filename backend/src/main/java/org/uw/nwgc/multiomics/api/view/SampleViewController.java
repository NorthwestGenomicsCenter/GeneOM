package org.uw.nwgc.multiomics.api.view;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sampleView")
public class SampleViewController {

    private final SampleViewService sampleViewService;

    public SampleViewController(SampleViewService sampleViewService) {
        this.sampleViewService = sampleViewService;
    }

    @GetMapping("/{sampleId}/views")
    public List<SampleViewDTO> getSampleViews(@PathVariable String sampleId) {
        return sampleViewService.getAvailableTechnologyViewPairsForSample(sampleId);
    }


    @GetMapping("/{sampleId}/tviews")
    public ResponseEntity<Map<String, List<String>>> getAvailableViewsByTechnologyForSample(
            @PathVariable String sampleId
    ) {
        Map<String, List<String>> result = sampleViewService.getAvailableViewsByTechnologyForSample(sampleId);
        return ResponseEntity.ok(result);
    }
}
