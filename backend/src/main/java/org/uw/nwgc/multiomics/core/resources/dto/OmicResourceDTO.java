package org.uw.nwgc.multiomics.core.resources.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueDTO;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.nio.file.Path;
import java.util.List;
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
    private String description;
    private String displayName;
    private Boolean igvTrack;
    private TissueDTO tissue;
    private List<String> visualizers;
    private List<String> visualizationComponents;

    public OmicResourceDTO(OmicResource omicResource) {
        this.omicResourceId = omicResource.getId();
        this.path = getPath(omicResource);
        this.visualizationComponents = omicResource.getVisualizers();
        this.description = omicResource.getDescription();
        this.displayName = omicResource.getDisplayName();
        this.igvTrack = omicResource.getIgvTrack();

        if (omicResource.getSample() != null) {
            this.sampleId = omicResource.getSample().getId();
        }

        if (omicResource.getVizTransformer() != null) {
            this.visualizers = omicResource.getVizTransformer()
                    .stream()
                    .map(viz -> {
                        if (viz != null) {
                            return viz.getSimpleName();
                        }

                        return null;
                    })
                    .toList();
        }

        var tissueType = omicResource.getTissueType();
        if (tissueType != null) {
            this.tissue = new TissueDTO(tissueType);
        } else {
            this.tissue = new TissueDTO();
        }
    }

    private String getPath(OmicResource omicResource) {
        if (omicResource.getPath() == null) return "";
        Path resolvedPath = FileUtil.resolvePath(omicResource.getPath());
        return resolvedPath.getFileName().toString();
    }

}
