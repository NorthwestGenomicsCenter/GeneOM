package org.uw.nwgc.multiomics.api.view;

public class SampleViewDTO {
    private String technology;
    private String viewName; // from enum

    public SampleViewDTO(String technology, String viewName) {
        this.technology = technology;
        this.viewName = viewName;
    }

    public String getTechnology() {
        return technology;
    }

    public String getViewName() {
        return viewName;
    }
}
