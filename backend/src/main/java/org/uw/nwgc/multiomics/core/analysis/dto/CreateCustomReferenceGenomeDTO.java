package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;
import org.uw.nwgc.multiomics.core.analysis.CustomReference;
import org.uw.nwgc.multiomics.core.resources.OmicResource;

@Data
public class CreateCustomReferenceGenomeDTO {
    String shortName;
    String longName;
    String filePath;
    String indexPath;

    public CustomReference createCustomReference() {
        CustomReference customReference = new CustomReference();
        customReference.setShortName(shortName);
        customReference.setLongName(longName);

        // File Path
        OmicResource omicResourceFilePath = new OmicResource();
        omicResourceFilePath.setPath(filePath);
        customReference.setFastaURL(omicResourceFilePath);

        // Index Path
        OmicResource omicResourceIndexPath = new OmicResource();
        omicResourceIndexPath.setPath(indexPath);
        customReference.setIndexURL(omicResourceIndexPath);

        return customReference;
    }
}
