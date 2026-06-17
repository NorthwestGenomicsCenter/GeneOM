package org.uw.nwgc.multiomics.core.resources;

import org.springframework.http.HttpHeaders;

import java.io.Reader;
import java.nio.file.Path;
import java.util.List;

public interface VizTransformer {
    List<String> visualizationComponents();
    Object serveResource(Reader reader, HttpHeaders header);
    boolean validateResource(Reader reader, String pathString);
}
