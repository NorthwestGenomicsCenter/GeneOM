package org.uw.nwgc.multiomics.assay;

import lombok.Getter;
import org.uw.nwgc.multiomics.assay.call_peek.CallPeakVisualizer;
import org.uw.nwgc.multiomics.assay.generic_table.GenericTableVisualizer;
import org.uw.nwgc.multiomics.assay.igv.Igv;
import org.uw.nwgc.multiomics.core.transformer.VizTransformer;

import java.util.Arrays;

public enum Transformer {
    CallPeak("Call Peak", CallPeakVisualizer.class),
    DifferentialExpressionTable("Differential Expression Table", DiffExpressionTableVisualizer.class),
    DiffExpressionScatterVisualizer("Differential Expression Scatter", DiffExpressionVisualizer.class),
    HIC("HiC", HiC.class),
    IGV("IGV", Igv.class),
    GenericTable("Generic Table", GenericTableVisualizer.class);

    @Getter
    private String displayName;
    private Class<? extends VizTransformer> clazz;

    Transformer(String displayName, Class<?> clazz) {
        this.displayName = displayName;
        this.clazz = (Class<? extends VizTransformer>) clazz;
    }

    public static Transformer fromString(String name) {
        return Arrays.stream(Transformer.values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Class<? extends VizTransformer> getClassName() {
        return (Class<? extends VizTransformer>) clazz;
    }

    public VizTransformer instantiate() {
        try {
            return (VizTransformer) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
