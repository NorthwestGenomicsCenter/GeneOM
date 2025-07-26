package org.uw.nwgc.multiomics.assay.igv;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;

import java.io.Reader;
import java.util.List;

@Component
public class Igv implements VizTransformer {
    @Override
    public List<String> visualizationComponents() {
        return List.of("IGV");
    }

    @Override
    public Object serveResource(Reader reader, HttpHeaders header) {
        return null;
    }

    @Override
    public boolean validateResource(Reader reader) {
        return false;
    }
}
