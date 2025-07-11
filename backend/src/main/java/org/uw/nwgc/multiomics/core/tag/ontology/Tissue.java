package org.uw.nwgc.multiomics.core.tag.ontology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.uw.nwgc.multiomics.core.tag.ontology.uberon.Definition;
import org.uw.nwgc.multiomics.core.tag.ontology.uberon.NodesItem;

@Entity
@Getter
@Setter
@Table(name = "tissue")
public class Tissue {

    @Id
    private String url;

    @Column(columnDefinition="TEXT")
    private String name;

    @Column(columnDefinition="TEXT")
    private String definition;

    private boolean featured = false;

    private String rawJson;

    private static ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public Tissue() {
    }

    public Tissue(NodesItem item) {
        this.url = item.getId();
        this.name = item.getLbl();
        var meta = item.getMeta();
        if (meta != null) {
            Definition def = meta.getDefinition();
            if (def != null) {
                this.definition = def.getVal();
            }
        }

        try {
            this.rawJson = mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
