package org.uw.nwgc.multiomics.core.tag.ontology;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TissueDTO {
    private String url;
    private String name;
    private String definition;
    private boolean featured = false;

    public TissueDTO(Tissue t) {
        this.url = t.getUrl();
        this.name = t.getName();
        this.definition = t.getDefinition();
        this.featured = t.isFeatured();
    }
}
