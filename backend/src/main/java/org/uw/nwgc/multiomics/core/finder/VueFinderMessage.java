package org.uw.nwgc.multiomics.core.finder;

import lombok.Data;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class VueFinderMessage {

    public static FinderMessageDTO buildFinderMessage(String adapter, String path) throws Exception {
        FinderMessageDTO message = new FinderMessageDTO();
        message.setAdapter(adapter);

        Path basePath = FinderBasePath.fromString(adapter).getPath();
        if (basePath == null) {
            throw new Exception("Unsupported Adapter for VueFinder");
        }

        // Get the relative path for the view
        if (path != null && !path.equals("null") && path.contains("://")) {
            path = path.split("://")[1];
        } else if (path == null || path.equals("null")) {
            path = "";
        }

        Path p = new UnixFile(basePath.toString(), path).toPath();

        // Normalize the paths
        p = p.normalize();
        if (!p.startsWith(basePath)) {
            throw new Exception("It appears the VueFinder path does not start with a base path, please check if a relative paths is correct");
        }

        // Set the directory name in the nav bar
        message.setDirname(adapter + "://" + path);

        if (Files.isDirectory(p)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(p)) {
                for (Path entry : directoryStream) {
                    // Create a VueFinderFile object for each entry in the directory
                    FinderFileDTO file = finderFile(entry, basePath, adapter);
                    message.getFiles().add(file);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to read directory: " + path, e);
            }
        }

        return message;
    }

    private static FinderFileDTO finderFile(Path fullPath, Path basePath, String adapter) {
        FinderFileDTO dto = new FinderFileDTO();

        dto.setAdapter(adapter);

        try {
            // Determine if the path is a directory or file
            dto.setType(Files.isDirectory(fullPath) ? "dir" : "file");

            // Set the path as a string
            dto.setPath(adapter + "://" + new UnixFile(basePath.relativize(fullPath)));

            // Set the basename
            dto.setBasename(fullPath.getFileName() != null ? fullPath.getFileName().toString() : "");

            // Set the extension (if it's a file)
            if (dto.getType().equals("file")) {
                String filename = fullPath.getFileName().toString();
                int dotIndex = filename.lastIndexOf('.');
                dto.setExtension(dotIndex > 0 ? filename.substring(dotIndex + 1) : "");
                dto.setFileSize(Files.size(fullPath));
            } else {
                dto.setExtension("");
            }

            // Set the last modified time
            BasicFileAttributes attrs = Files.readAttributes(fullPath, BasicFileAttributes.class);
            FileTime lastModifiedTime = attrs.lastModifiedTime();
            dto.setLastModified((int) (lastModifiedTime.toMillis() / 1000));

        } catch (Exception e) {
            throw new RuntimeException("Failed to create VueFinderFile from Path", e);
        }

        return dto;
    }

    /**
     * Queries subfolders within a specified adapter and path. The adapter is the base path from the
     * {@link FinderBasePath}. This is used to obscure the real path from the users of the application
     *
     * @param pathString The Path String
     * @param adapter The adapter type (e.g., "sharing").
     * @return A map containing a list of subfolders under the "folders" key.
     */
    public static Map<String, List<FinderFolderDTO>> querySubfolders(String pathString, String adapter) {
        pathString = pathString.replace(adapter + "://", "");

        FinderBasePath base = FinderBasePath.valueOf(adapter);

        Path basePath = base.getPath();
        Path folderPath = Path.of(base.getText(), pathString);

        List<FinderFolderDTO> folders = new ArrayList<>();
        try {
            // The initial path must be a directory for this function
            if (!Files.isDirectory(folderPath)) throw new Exception("Subfolders query must happen on a directory");

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
                for (Path entry : directoryStream) {
                    // Create a VueFinderFile object for each entry in the directory
                    if (Files.isDirectory(entry)) {
                        String relativePath = adapter + "://" + new UnixFile(basePath.relativize(entry).toString());

                        FinderFolderDTO dir = FinderFolderDTO.builder()
                                .adapter(adapter)
                                .basename(entry.getFileName().toString())
                                .path(relativePath)
                                .build();
                        folders.add(dir);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to read directory: " + folderPath, e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create VueFinderFile from Path", e);
        }

        return Map.of("folders", folders);
    }

}
    