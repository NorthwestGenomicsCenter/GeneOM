package org.uw.nwgc.multiomics.core.transformer;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface StreamSource {
    InputStream open() throws IOException;
}
