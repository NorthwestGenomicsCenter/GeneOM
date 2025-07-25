package org.uw.nwgc.multiomics.api.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SampleViewDTO {
    private String technology;
    private String viewName; // from enum
    private UUID omicResourceId;
    private String fileName;

    public static List<SampleViewDTO> mapResource(OmicResource resource) {
        List<SampleViewDTO> dtos = new ArrayList<>();

        for (var transformer : resource.getVizTransformer()) {
            SampleViewDTO dto = new SampleViewDTO();
            dto.technology = "technology";
            dto.viewName = transformer.getSimpleName();
            dto.omicResourceId = resource.getId();
            dto.fileName = FileUtil.resolvePath(resource.getPath()).getFileName().toString();
            dtos.add(dto);
        }

        return dtos;
    }

    public SampleViewDTO(String technology, String viewName) {
        this.technology = technology;
        this.viewName = viewName;
    }
}
