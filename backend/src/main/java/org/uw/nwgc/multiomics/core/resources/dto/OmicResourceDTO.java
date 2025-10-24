package org.uw.nwgc.multiomics.core.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueDTO;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OmicResourceDTO {
    private UUID omicResourceId;
    private UUID sampleId;
    private String integrationSource;
    private String path;
    private Boolean igvTrack;
    private TissueDTO tissue;
    private List<String> visualizers;
    private List<String> visualizationComponents;

    public OmicResourceDTO(OmicResource omicResource) {
        this.omicResourceId = omicResource.getId();
        if (omicResource.getSample() != null) {
            this.sampleId = omicResource.getSample().getId();
        }
        this.path = omicResource.getPath();
        this.visualizationComponents = omicResource.getVisualizers();
        if (omicResource.getVizTransformer() != null) {
            this.visualizers = omicResource.getVizTransformer()
                    .stream()
                    .map(Class::getSimpleName)
                    .toList();
        }
        this.igvTrack = omicResource.getIgvTrack();

        var tissueType = omicResource.getTissueType();
        if (tissueType != null) {
            this.tissue = new TissueDTO(tissueType);
        } else {
            this.tissue = new TissueDTO();
        }
    }

}
