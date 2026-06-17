package org.uw.nwgc.multiomics.service.assay_detector.detectors;

import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.service.assay_detector.AbstractAssayDetector;
import org.uw.nwgc.multiomics.service.assay_detector.types.AssayType;
import org.uw.nwgc.multiomics.service.assay_detector.types.FilePattern;
import org.uw.nwgc.multiomics.service.assay_detector.types.ResourceType;

import java.util.List;

@Component
public class ChiaTacDetector extends AbstractAssayDetector {

    @Override
    public AssayType getAssayType() {
        return AssayType.CHIATAC;
    }

    /**
     * Required patterns are the minimal set that uniquely fingerprints a ChIATAC directory.
     * Weights reflect diagnostic value — high for ChIATAC-specific files, low for generic ones.
     */
    @Override
    public List<FilePattern> getPatterns() {
        return List.of(
                // --- Required ---
                new FilePattern("forBASIC.bam",                                         true,  5, ResourceType.BAM,          "Main alignment BAM"),
                new FilePattern("forBASIC.bam.bai",                                     true,  2, ResourceType.BAI,          "Main BAM index"),
                new FilePattern("singlelinker.paired.UU.bam",                           true,  5, ResourceType.BAM,          "Unique paired reads BAM"),
                new FilePattern("ciftif.rda",                                           true,  5, ResourceType.OTHER,        "CIF/TIF R data — unique to ChIATAC pipeline"),

                // --- Interactions ---
                new FilePattern("SA.itx",                                               false, 4, ResourceType.INTERACTIONS, "Same-anchor interactions"),
                new FilePattern("DA.itx",                                               false, 4, ResourceType.INTERACTIONS, "Different-anchor interactions"),
                new FilePattern("wChIP.itx",                                            false, 4, ResourceType.INTERACTIONS, "ChIP-weighted interactions"),
                new FilePattern("bedpe.annotation.xls",                                 false, 3, ResourceType.INTERACTIONS, "Annotated BEDPE interactions"),
                new FilePattern("e500.clusters.cis.chiasig.gz",                         false, 3, ResourceType.INTERACTIONS, "Cis ChIA-sig clusters"),
                new FilePattern("e500.clusters.trans.chiasig.gz",                       false, 3, ResourceType.INTERACTIONS, "Trans ChIA-sig clusters"),
                new FilePattern("e500.clusters.cis.chiasig.BE2",                        false, 2, ResourceType.INTERACTIONS, "BE2 cis ChIA-sig clusters"),
                new FilePattern("e500.clusters.cis.chiasig.BE2.sigf.interactions",      false, 3, ResourceType.INTERACTIONS, "Significant filtered interactions"),
                new FilePattern("e500.juice.gz",                                        false, 2, ResourceType.INTERACTIONS, "Juicer format interactions"),
                new FilePattern("pooled.clusters.gz",                                   false, 2, ResourceType.INTERACTIONS, "Pooled interaction clusters"),

                // --- Bedgraph / coverage ---
                new FilePattern("ave_nCIF.bedgraph.gz",                                 false, 3, ResourceType.BEDGRAPH,     "Average nCIF bedgraph"),
                new FilePattern("ave_nCIF.noblack.bedgraph.gz",                         false, 2, ResourceType.BEDGRAPH,     "Average nCIF bedgraph (no blacklist)"),
                new FilePattern("ave_nTIF.bedgraph.gz",                                 false, 3, ResourceType.BEDGRAPH,     "Average nTIF bedgraph"),
                new FilePattern("ave_nTIF.noblack.bedgraph.gz",                         false, 2, ResourceType.BEDGRAPH,     "Average nTIF bedgraph (no blacklist)"),
                new FilePattern("forBASIC.sorted.bedgraph.gz",                          false, 2, ResourceType.BEDGRAPH,     "Sorted bedgraph for BASIC"),
                new FilePattern("treat_pileup.clip.sorted.bdg",                         false, 2, ResourceType.BEDGRAPH,     "MACS2 treatment pileup bedgraph"),
                new FilePattern("coverage.forBASIC.bw",                                 false, 2, ResourceType.BIGWIG,       "Coverage bigwig for BASIC"),
                new FilePattern("treat_pileup.NDP.bw",                                  false, 2, ResourceType.BIGWIG,       "NDP normalised pileup bigwig"),

                // --- Peaks ---
                new FilePattern("peaks.narrowPeak",                                     false, 3, ResourceType.PEAKS,        "MACS2 narrow peaks"),
                new FilePattern("peaks.xls",                                            false, 1, ResourceType.PEAKS,        "MACS2 peaks summary"),
                new FilePattern("summits.bed",                                          false, 2, ResourceType.BED,          "Peak summits BED"),

                // --- BAMs ---
                new FilePattern("none.F2048.bam",                                       false, 2, ResourceType.BAM,          "None linker F2048 filtered BAM"),
                new FilePattern("none.F2048.bam.bai",                                   false, 1, ResourceType.BAI,          "None linker F2048 BAM index"),
                new FilePattern("none.nr.sorted.bam",                                   false, 2, ResourceType.BAM,          "None linker non-redundant sorted BAM"),
                new FilePattern("none.UU.bam",                                          false, 2, ResourceType.BAM,          "None linker unique pairs BAM"),
                new FilePattern("none.UU.intra.3000.bam",                               false, 2, ResourceType.BAM,          "None linker intra-chromosomal (>3kb) BAM"),
                new FilePattern("singlelinker.paired.F2048.bam",                        false, 2, ResourceType.BAM,          "Paired singlelinker F2048 BAM"),
                new FilePattern("singlelinker.paired.nr.sorted.bam",                    false, 2, ResourceType.BAM,          "Paired singlelinker non-redundant sorted BAM"),
                new FilePattern("singlelinker.paired.UU.intra.8000.bam",                false, 2, ResourceType.BAM,          "Paired singlelinker intra-chromosomal (>8kb) BAM"),
                new FilePattern("singlelinker.paired.UxxU.bam",                         false, 2, ResourceType.BAM,          "Paired singlelinker UxxU BAM"),
                new FilePattern("singlelinker.single.F2048.bam",                        false, 2, ResourceType.BAM,          "Single singlelinker F2048 BAM"),
                new FilePattern("singlelinker.single.nr.sorted.bam",                    false, 2, ResourceType.BAM,          "Single singlelinker non-redundant sorted BAM"),
                new FilePattern("singlelinker.single.UU.bam",                           false, 2, ResourceType.BAM,          "Single singlelinker UU BAM"),

                // --- FASTQs ---
                new FilePattern("none.fastq.gz",                                        false, 1, ResourceType.FASTQ,        "None linker FASTQ"),
                new FilePattern("conflict.fastq.gz",                                    false, 1, ResourceType.FASTQ,        "Conflict reads FASTQ"),
                new FilePattern("tied.fastq.gz",                                        false, 1, ResourceType.FASTQ,        "Tied reads FASTQ"),
                new FilePattern("singlelinker.paired.fastq.gz",                         false, 1, ResourceType.FASTQ,        "Paired singlelinker FASTQ"),
                new FilePattern("singlelinker.single.fastq.gz",                         false, 1, ResourceType.FASTQ,        "Single singlelinker FASTQ"),

                // --- Stats ---
                new FilePattern("final_stats.tsv",                                      false, 2, ResourceType.STATS,        "Final pipeline statistics"),
                new FilePattern("none.stat.xls",                                        false, 1, ResourceType.STATS,        "None linker statistics"),
                new FilePattern("singlelinker.paired.stat.xls",                         false, 1, ResourceType.STATS,        "Paired singlelinker statistics"),
                new FilePattern("singlelinker.single.stat.xls",                         false, 1, ResourceType.STATS,        "Single singlelinker statistics"),

                // --- Other ---
                new FilePattern("hic",                                                  false, 2, ResourceType.HIC,        "Hi-C format file"),
                new FilePattern("genome.length",                                        false, 1, ResourceType.OTHER,        "Genome chromosome lengths")
        );
    }
}
