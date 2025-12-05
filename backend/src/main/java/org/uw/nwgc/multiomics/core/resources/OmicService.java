package org.uw.nwgc.multiomics.core.resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.uw.nwgc.multiomics.assay.igv.Igv;
import org.uw.nwgc.multiomics.core.admin.configuration.ConfigProperties;
import org.uw.nwgc.multiomics.core.cohort.Cohort;
import org.uw.nwgc.multiomics.core.resources.dto.EditOmicResourceDTO;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class OmicService {

    private final OmicRepo omicRepo;

    public OmicResource findById(UUID id) {
        return omicRepo.findById(id).orElse(null);
    }

    public OmicResource save(OmicResource resource){
        return omicRepo.save(resource);
    }

    public void delete(OmicResource resource) {
        omicRepo.delete(resource);
    }

    /**
     * Handles the upload and persistence of an {@link OmicResource} file associated with a {@link Sample}.
     * <p>
     * This method writes the uploaded multipart file to the sample's root directory on disk,
     * creates a new {@code OmicResource} entity pointing to the saved file,
     * associates it with the provided {@code Sample}, and persists it.
     * </p>
     *
     * <p><strong>Behavior:</strong></p>
     * <ul>
     *   <li>Creates the sample directory under {@link ConfigProperties#SAMPLES_ROOT_DIRECTORY} if it does not exist.</li>
     *   <li>Writes the file contents to that directory, overwriting any existing file with the same name.</li>
     *   <li>Creates an {@link OmicResource} record linked to the {@link Sample} and assigns
     *       a default visualizer transformer ({@link Igv}).</li>
     *   <li>Persists the {@code OmicResource} using {@link #save(OmicResource)}.</li>
     * </ul>
     *
     * @param file   the uploaded {@link MultipartFile} to save on disk; must not be {@code null}
     * @param sample the {@link Sample} entity this file belongs to; must not be {@code null}
     * @return the persisted {@link OmicResource} representing the uploaded file
     * @throws IOException if the sample directory cannot be created or the file cannot be written
     */
    public OmicResource uploadFile(MultipartFile file, Sample sample) throws IOException {
        File sampleRoot = new File(ConfigProperties.SAMPLES_ROOT_DIRECTORY.toFile(), sample.getId().toString());
        if  (!sampleRoot.exists()) {
            boolean createdDirectory = sampleRoot.mkdirs();
            if  (!createdDirectory) {
                throw new IOException("Unable to create sample directory");
            }
        }

        Set<OmicResource> resources = sample.getOmicResources();
        boolean fileExists = resources
                .stream()
                .anyMatch(resource -> {
                    Path path = FileUtil.resolvePath(resource.getPath());
                    return path.endsWith(file.getOriginalFilename());
                });

        if (fileExists) {
            throw new IOException("File already exists, rename or remove conflicting file");
        }

        Path dataFilePath = sampleRoot.toPath().resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.write(dataFilePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Uploaded omic resource '{}' for sample {}", file.getOriginalFilename(), sample.getId());

        OmicResource resource = new OmicResource();
        resource.setSample(sample);
        resource.setPath(dataFilePath.toString());
        resource.setVizTransformer(List.of(Igv.class));
        sample.getOmicResources().add(resource);
        return save(resource);
    }

    /**
     * Handles the upload and persistence of an {@link OmicResource} file associated with a {@link Sample}.
     * <p>
     * This method writes the uploaded multipart file to the sample's root directory on disk,
     * creates a new {@code OmicResource} entity pointing to the saved file,
     * associates it with the provided {@code Sample}, and persists it.
     * </p>
     *
     * <p><strong>Behavior:</strong></p>
     * <ul>
     *   <li>Creates the sample directory under {@link ConfigProperties#SAMPLES_ROOT_DIRECTORY} if it does not exist.</li>
     *   <li>Writes the file contents to that directory, overwriting any existing file with the same name.</li>
     *   <li>Creates an {@link OmicResource} record linked to the {@link Sample} and assigns
     *       a default visualizer transformer ({@link Igv}).</li>
     *   <li>Persists the {@code OmicResource} using {@link #save(OmicResource)}.</li>
     * </ul>
     *
     * @param file   the uploaded {@link MultipartFile} to save on disk; must not be {@code null}
     * @param cohort the {@link Cohort} entity this file belongs to; must not be {@code null}
     * @return the persisted {@link OmicResource} representing the uploaded file
     * @throws IOException if the sample directory cannot be created or the file cannot be written
     */
    public OmicResource uploadFile(MultipartFile file, Cohort cohort) throws IOException {
        File sampleRoot = new File(ConfigProperties.SAMPLES_ROOT_DIRECTORY.toFile(), cohort.getId().toString());
        if  (!sampleRoot.exists()) {
            boolean createdDirectory = sampleRoot.mkdirs();
            if  (!createdDirectory) {
                throw new IOException("Unable to create cohort directory");
            }
        }

        Set<OmicResource> resources = cohort.getOmicResources();
        boolean fileExists = resources
                .stream()
                .anyMatch(resource -> {
                    Path path = FileUtil.resolvePath(resource.getPath());
                    return path.endsWith(file.getOriginalFilename());
                });

        if (fileExists) {
            throw new IOException("File already exists, rename or remove conflicting file");
        }

        Path dataFilePath = sampleRoot.toPath().resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.write(dataFilePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Uploaded omic resource '{}' for sample {}", file.getOriginalFilename(), cohort.getId());

        OmicResource resource = new OmicResource();
        resource.setPath(dataFilePath.toString());
        resource.setVizTransformer(List.of(Igv.class));
        cohort.getOmicResources().add(resource);
        return save(resource);
    }

    public Path findIndexFilePath(String requestedFileName, Path resolvedResourcePath) {
        if (requestedFileName.endsWith("bai") && !resolvedResourcePath.getFileName().toString().endsWith(".bai")) {
            Path index = Path.of(resolvedResourcePath.toString() + ".bai");
            if (index.toFile().exists()) {
                return index;
            }

            index = Path.of(resolvedResourcePath.toString().replace(".bam", "") + ".bai");
            if (index.toFile().exists()) {
                return index;
            }

            return null;
        }

        return resolvedResourcePath;
    }

    public OmicResource edit(OmicResource resource, EditOmicResourceDTO dto) {
        resource.setDescription(dto.description());
        resource.setDisplayName(dto.displayName());

        return save(resource);
    }
}
