package org.uw.nwgc.multiomics.core.finder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class FinderFileDTO {
    private String adapter;
    private String type;
    private String path;

    @JsonProperty("last_modified")
    private int lastModified;

    @JsonProperty("file_size")
    private long fileSize = 0L;

    private String basename;
    private String extension;

    @JsonProperty("extra_metadata")
    private ArrayList<Object> extraMetadata = new ArrayList<>();

    private String visibility = "public";
    private String storage = "local";
}