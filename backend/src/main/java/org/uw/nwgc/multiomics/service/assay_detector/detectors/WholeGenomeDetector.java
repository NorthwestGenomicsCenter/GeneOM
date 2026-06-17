package org.uw.nwgc.multiomics.service.assay_detector.detectors;

import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.service.assay_detector.AbstractAssayDetector;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayType;
import org.uw.nwgc.multiomics.service.assay_detector.types.FilePattern;
import org.uw.nwgc.multiomics.service.assay_detector.types.ResourceType;

import java.util.List;

@Component
public class WholeGenomeDetector extends AbstractAssayDetector {

    @Override
    public AssayType getAssayType() {
        return AssayType.WHOLE_GENOME;
    }

    /**
     * Required patterns are the minimal set that uniquely fingerprints a NWGC WHOLE_GENOME directory.
     * Weights reflect diagnostic value — high for WHOLE_GENOME-specific files
     */

    @Override
    public List<FilePattern> getPatterns() {
        return List.of(
                // --- Required ---
                new FilePattern("WHOLE_GENOME.filtered.g.vcf.gz",       true, 5, ResourceType.VCF, "Filtered Genomic VCF"),
                new FilePattern("WHOLE_GENOME.filtered.g.vcf.gz.tbi",   true, 5, ResourceType.TBI, "Index for Filtered Genomic VCF"),
                new FilePattern("WHOLE_GENOME.main.g.vcf.gz",           true, 5, ResourceType.VCF, "Unfiltered Genomic VCF"),
                new FilePattern("WHOLE_GENOME.main.g.vcf.gz.tbi",       true, 5, ResourceType.TBI, "Index for Unfiltered Genomic VCF"),
                new FilePattern("WHOLE_GENOME.merged.bam",              true, 5, ResourceType.BAM, "Merged Bam File"),
                new FilePattern("WHOLE_GENOME.merged.bam.bai",          true, 5, ResourceType.BAI, "Index for Merged Bam File")
        );
    }
}
