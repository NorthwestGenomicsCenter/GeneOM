package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.uw.nwgc.multiomics.core.analysis.CustomReference;
import org.uw.nwgc.multiomics.util.FileUtil;

@Getter
@Setter
@AllArgsConstructor
public class CustomReferenceDTO {

    String id;
    String name;
    String fastaURL;
    String indexURL;

    public CustomReferenceDTO(CustomReference ref, String baseUrl) {
        this.id = ref.getShortName();
        this.name = ref.getLongName();
        this.fastaURL = baseUrl + "/api/omic/igv/" + ref.getFastaURL().getId().toString() + "/" + FileUtil.resolveFilename(ref.getFastaURL().getPath());
        this.indexURL = baseUrl + "/api/omic/igv/" + ref.getIndexURL().getId().toString() + "/" + FileUtil.resolveFilename(ref.getIndexURL().getPath());
    }
}
