package org.uw.nwgc.multiomics.core.resources;

import java.io.Reader;
import java.util.List;

public interface VizTransformer {
    List<String> visualizationComponents();
    Object serveResource(Reader reader);
}
