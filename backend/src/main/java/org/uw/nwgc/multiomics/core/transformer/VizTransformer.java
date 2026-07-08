package org.uw.nwgc.multiomics.core.transformer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public interface VizTransformer {
    List<String> visualizationComponents();
//    Object serveResource(Reader reader, HttpHeaders header);
    Object serveResource(ServableResource servableResource) throws IOException;
    boolean validateResource(Reader reader, String pathString);
}
