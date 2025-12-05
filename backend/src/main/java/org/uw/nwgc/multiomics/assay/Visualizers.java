package org.uw.nwgc.multiomics.assay;

import org.uw.nwgc.multiomics.core.resources.VizTransformer;

public enum Visualizers {
    DifferentialExpressionTable("Differential Expression", DiffExpressionVisualizer.class);

    private String displayName;
    private Class<?> clazz;

    Visualizers(String displayName, Class<?> clazz) {
        this.displayName = displayName;
        this.clazz = clazz;
    }

    public VizTransformer instantiate() {
        try {
            return (VizTransformer) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
