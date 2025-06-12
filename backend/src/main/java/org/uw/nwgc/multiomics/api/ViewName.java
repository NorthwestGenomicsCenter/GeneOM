package org.uw.nwgc.multiomics.api;

public enum ViewName {
    DIFFERENTIAL_EXPRESSION("differential-expression"),
    DIFFERENTIAL_PEAK("differential-peak"),
    IGV("igv");

    private final String apiValue;

    private ViewName(String text) { this.apiValue = text; }

    public String getApiValue() { return apiValue; }
}
