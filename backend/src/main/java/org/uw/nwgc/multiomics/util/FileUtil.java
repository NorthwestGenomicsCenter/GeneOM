package org.uw.nwgc.multiomics.util;

import org.uw.nwgc.multiomics.core.finder.FinderBasePath;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static Path resolvePath(String filename) {
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

    public static BufferedReader readResourcePath(String filename) throws IOException {
        Path fullPath = resolvePath(filename);
        return Files.newBufferedReader(fullPath);
    }
}
