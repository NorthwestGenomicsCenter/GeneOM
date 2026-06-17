package org.uw.nwgc.multiomics.service.assay_detector.types;

import org.uw.nwgc.multiomics.assay.HiC;
import org.uw.nwgc.multiomics.assay.igv.Igv;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ResourceType {
    BAM(Igv.class),
    BAI(Igv.class),
    BEDGRAPH(Igv.class),
    BIGWIG(Igv.class),
    BED(Igv.class),
    HIC(HiC.class),
    PEAKS(Igv.class),
    INTERACTIONS(Igv.class),
    STATS(Igv.class),
    FASTQ(),
    OTHER(),
    VCF(Igv.class),
    TBI(Igv.class);

    final List<Class<? extends VizTransformer>> transformers;

    @SafeVarargs
    ResourceType(Class<? extends VizTransformer> ...transformers) {
        if (transformers == null || transformers.length == 0) {
            this.transformers = new ArrayList<>();
        } else {
           this.transformers = Arrays.asList(transformers);
        }
    }

    public List<Class<? extends VizTransformer>> getTransformers() {
        return transformers;
    }
}