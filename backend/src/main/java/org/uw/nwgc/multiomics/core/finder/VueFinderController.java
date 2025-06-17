package org.uw.nwgc.multiomics.core.finder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vuefinder")
public class VueFinderController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/")
    public ResponseEntity<Object> searchNoPathParam(
            @RequestParam("q") String query,
            @RequestParam String adapter,
            @RequestParam(required = false) String path
    ) throws Exception {
        Object msg = null;

        if (adapter == null || adapter.equals("null")) {
            adapter = FinderBasePath.nobackup.name();
        }

        if (query != null && query.equals("index")) {
            msg = VueFinderMessage.buildFinderMessage(adapter, path);
        } else if (query != null && query.equals("subfolders")) {
            msg = VueFinderMessage.querySubfolders(path, adapter);
        } else if (query != null) {
            throw new Exception("Vue Finder query not implemented yet:" + query);
        }

        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

}
