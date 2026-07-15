package org.uw.nwgc.multiomics.core.transformer;

import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public record ServableResource(
        StreamSource streamSource,
        Path path,
        HttpHeaders header
) {
    public Reader getReader() throws IOException {
        return getReader(StandardCharsets.UTF_8);
    }

    public Reader getReader(Charset charset) throws IOException {
        return new InputStreamReader(streamSource.open(), charset);
    }
}
