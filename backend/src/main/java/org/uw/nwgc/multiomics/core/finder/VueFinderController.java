package org.uw.nwgc.multiomics.core.finder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.service.assay_detector.AssayDetectionService;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayDetectionResult;

import java.util.List;

@RestController
@RequestMapping("/vuefinder")
public class VueFinderController {

    AssayDetectionService assayDetectionService;
    public VueFinderController(AssayDetectionService assayDetectionService) {
        this.assayDetectionService = assayDetectionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/")
    public ResponseEntity<Object> searchNoPathParam(
            @RequestParam("q") String query,
            @RequestParam String adapter,
            @RequestParam(required = false) String path
    ) throws Exception {
        Object msg = null;

        if (adapter == null || adapter.toLowerCase().equals("null")) {
            adapter = FinderBasePath.nobackup.name();
        }

        if (query != null && query.equals("index")) {
            msg = VueFinderMessage.buildFinderMessage(adapter, path);
        } else if (query != null && query.equals("subfolders")) {
            msg = VueFinderMessage.querySubfolders(path, adapter);
        } else if (query != null) {
            throw new Exception("Vue Finder query not implemented yet:" + query);
        }

        String importDirectory = null;
        String assayType = null;
        List<AssayDetectionResult> assays = assayDetectionService.investigateDirectory(VueFinderMessage.resolvePath(adapter, path));
        if (!assays.isEmpty()) {
            importDirectory = VueFinderMessage.resolvePath(adapter, path).toString();
            assayType = assays.getFirst().assayType().name();
        }

        VueFinderResponseDTO dto = new VueFinderResponseDTO(
                importDirectory,
                assayType,
                msg
        );

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
