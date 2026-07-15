package org.uw.nwgc.multiomics.core.finder;

import lombok.Getter;
import java.nio.file.Path;

@Getter
public enum FinderBasePath {
//    sample("/net/nwgc/vol1/data/processed/samples/"),
    mendelian_projects("/net/nwgc/vol1/mendelian_projects/"),
    gene_om("/net/nwgc/vol2/data/processed/geneom/"),
    sharing("/net/nwgc/vol1/sharing"),
    nobackup("/net/nwgc/vol1/nobackup"),
    techdev("/net/nwgc/vol1/techdev"),
    temp("/temp");

    private final String text;

    FinderBasePath(String text) {
        this.text = text;
    }

    public Path getPath() {
        if (this.getText() != null) {
            return Path.of(this.getText());
        }

        return null;
    }

    public static FinderBasePath fromString(String text) {
        if (text != null) {
            for (FinderBasePath path : FinderBasePath.values()) {
                if (text.equalsIgnoreCase(path.text) || text.equalsIgnoreCase(path.name())) {
                    return path;
                }
            }
        }

        return null;
    }
}
