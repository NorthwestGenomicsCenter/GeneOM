package org.uw.nwgc.multiomics.core.tag.ontology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.uw.nwgc.multiomics.core.tag.ontology.uberon.GraphsItem;
import org.uw.nwgc.multiomics.core.tag.ontology.uberon.UberonOntology;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImportHumanPhenotypeOntology {

    public static List<Tissue> importData() {
        File file = new File("/temp/uberon.json");
        return importData(file);
    }

    public static List<Tissue> importData(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .findAndRegisterModules();
            UberonOntology ontology = mapper.readValue(file, UberonOntology.class);
            return ontology.getGraphs()
                    .stream()
                    .map(GraphsItem::getNodes)
                    .flatMap(List::stream)
                    .map(Tissue::new)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Tissue> importData(InputStream in) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .findAndRegisterModules();
            UberonOntology ontology = mapper.readValue(in, UberonOntology.class);
            return ontology.getGraphs()
                    .stream()
                    .map(GraphsItem::getNodes)
                    .flatMap(List::stream)
                    .map(Tissue::new)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
