package org.uw.nwgc.multiomics.core.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResponseDTO {

    public String label;
    public List<Object> items = new ArrayList<>();

    public SearchResponseDTO(String label) {
        this.label = label;
    }

    public void add(Object o) {
        this.items.add(o);
    }

    public void addAll(List<?> objs) {
        this.items.addAll(objs);
    }
}
