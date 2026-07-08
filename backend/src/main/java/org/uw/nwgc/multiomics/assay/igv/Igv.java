package org.uw.nwgc.multiomics.assay.igv;

import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.transformer.ServableResource;
import org.uw.nwgc.multiomics.core.transformer.VizTransformer;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class Igv implements VizTransformer {
    @Override
    public List<String> visualizationComponents() {
        return List.of("IGV");
    }

    @Override
    public Object serveResource(ServableResource servableResource) throws IOException {
        return null;
    }

    @Override
    public boolean validateResource(Reader reader, String pathString) {
        Path resolvedPath = FileUtil.resolvePath(pathString);
        String lowerPath = resolvedPath.toString().toLowerCase();

        // BAM: requires .bai index
        if (lowerPath.endsWith(".bam")) {
            return Files.exists(Path.of(resolvedPath + ".bai"));
        }

        // CRAM: requires .crai index
        if (lowerPath.endsWith(".cram")) {
            return Files.exists(Path.of(resolvedPath + ".crai"));
        }

        // FASTA: requires .fai index
        if (lowerPath.endsWith(".fasta") || lowerPath.endsWith(".fa") || lowerPath.endsWith(".fna")) {
            return Files.exists(Path.of(resolvedPath + ".fai"));
        }

        // Text formats: accept tabix (.gz.tbi) or tribble (.idx) index
        if (lowerPath.endsWith(".bed") || lowerPath.endsWith(".gff3") ||
                lowerPath.endsWith(".gtf") || lowerPath.endsWith(".vcf")) {
            return Files.exists(Path.of(resolvedPath + ".gz.tbi"))
                    || Files.exists(Path.of(resolvedPath + ".idx"));
        }

        // bedgraph: tabix index only
        if (lowerPath.endsWith(".bedgraph")) {
            return Files.exists(Path.of(resolvedPath + ".gz.tbi"));
        }

        // Formats that are self-contained (pre-indexed binary or no index needed)
        if (lowerPath.endsWith(".bigwig") || lowerPath.endsWith(".bw")  ||
                lowerPath.endsWith(".bigbed") || lowerPath.endsWith(".bb")  ||
                lowerPath.endsWith(".tdf")    || lowerPath.endsWith(".seg") ||
                lowerPath.endsWith(".bedpe")  || lowerPath.endsWith(".maf") ||
                lowerPath.endsWith(".mut")    || lowerPath.endsWith(".gwas") ||
                lowerPath.endsWith(".wig")    || lowerPath.endsWith(".bp")  ||
                lowerPath.endsWith(".narrowpeak") || lowerPath.endsWith(".broadpeak") ||
                lowerPath.endsWith(".interact")) {
            return Files.exists(resolvedPath);
        }

        return false;
    }
}
