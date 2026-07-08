package org.uw.nwgc.multiomics.util;

import org.uw.nwgc.multiomics.assay.Transformer;
import org.uw.nwgc.multiomics.core.finder.FinderBasePath;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.transformer.VizTransformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static Path resolveFilename(OmicResource resource) {
        Path path = resolvePath(resource.getPath());

        return path.getFileName();
    }

    public static Path resolveFilename(String filename) {
        Path path = resolvePath(filename);

        return path.getFileName();
    }

    public static Path resolvePath(OmicResource resource) {
        return resolvePath(resource.getPath());
    }

    public static Path resolvePath(String filename) {
        if (filename == null) {
            return null;
        }

        Path fullPath;
        if (filename.contains("://")) {
            String[] pathParts = filename.split("://");
            String basePath = FinderBasePath.fromString(pathParts[0]).getText();
            fullPath = Path.of(basePath, pathParts[1]);
        } else {
            fullPath = Path.of(filename);
        }

        return fullPath;
    }

    public static BufferedReader readResourcePath(OmicResource resource) throws IOException {
        Path fullPath = resolvePath(resource.getPath());
        return Files.newBufferedReader(fullPath);
    }

    public static BufferedReader readResourcePath(String filename) throws IOException {
        Path fullPath = resolvePath(filename);
        return Files.newBufferedReader(fullPath);
    }

    public static InputStream resourcePathInputStream(String filename) throws IOException {
        Path fullPath = resolvePath(filename);
        return Files.newInputStream(fullPath);
    }

    public static List<Class<? extends VizTransformer>> findVisualizationTransformers(String filename) throws IOException {
        List<Class<? extends VizTransformer>> transformers = new ArrayList<>();

        for (Transformer transformer : Transformer.values()) {
            VizTransformer viz = transformer.instantiate();

            try (Reader fileReader = FileUtil.readResourcePath(filename)) {
                assert viz != null;
                boolean valid = viz.validateResource(fileReader, filename);
                if (valid) {
                    transformers.add(transformer.getClassName());
                }
            }
        }

        return transformers;
    }
}
