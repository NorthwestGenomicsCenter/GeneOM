package org.uw.nwgc.multiomics.core.finder;

import java.io.File;
import java.nio.file.Path;

public class UnixFile extends File {
    public UnixFile(String pathname) {
        super(pathname);
    }

    public UnixFile(Path path) {
        super(path.toString());
    }

    public UnixFile(String root, String pathname) {
        super(root, pathname);
    }
    public UnixFile(File file, String pathname) {
        super(file, pathname);
    }

    @Override
    public String toString() {
        String path = super.toString();

        // Replace Windows-style backslashes with Unix-style forward slashes
        path = path.replace('\\', '/');

        return path;
    }
}
