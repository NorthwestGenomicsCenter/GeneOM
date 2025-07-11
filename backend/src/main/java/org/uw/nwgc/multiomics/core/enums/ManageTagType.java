package org.uw.nwgc.multiomics.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public enum ManageTagType {
    TECHNOLOGY("Technology"),
    TISSUE("Tissue");

    String text;

    ManageTagType(String text) {
        this.text = text;
    }

    @JsonValue
    public Map<String, String> toJson() {
        return Map.of(
                "key", this.name(),
                "value", this.text
        );
    }
}
