package org.uw.nwgc.multiomics.assay.call_peek;

import lombok.Data;
import org.uw.nwgc.multiomics.core.interfaces.Coordinatable;

@Data
public class CallPeak implements Coordinatable {

    private String chr;
    private long start;
    private long end;
    private int length;
    private long absSummit;
    private double pileup;
    private double log10Pvalue;
    private double foldEnrichment;
    private double log10Qvalue;
    private String name;

    @Override
    public String getGenomicCoordinate() {
        return chr + ":" + start + "-" + end;
    }
}
