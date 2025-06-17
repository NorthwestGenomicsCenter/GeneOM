package org.uw.nwgc.multiomics.core.finder;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class FinderMessageDTO {
    private String adapter = "";
    private String dirname = "/";
    private List<FinderFileDTO> files = new ArrayList<>();
    private List<String> storages = Arrays.stream(FinderBasePath.values())
            .map(FinderBasePath::name)
            .toList();
}
