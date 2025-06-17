package org.uw.nwgc.multiomics.core.finder;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FinderFileDTO {
    private String adapter;
    private String type;
    private String path;
    private int lastModified;
    private long fileSize;
    private String basename;
    private String extension;
    private ArrayList<Object> extraMetadata = new ArrayList<>();
    private String visibility = "public";
    private String storage = "local";

}
