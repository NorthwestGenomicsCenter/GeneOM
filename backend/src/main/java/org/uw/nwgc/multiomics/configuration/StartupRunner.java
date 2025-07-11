package org.uw.nwgc.multiomics.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.uw.nwgc.multiomics.core.genes.Gene;
import org.uw.nwgc.multiomics.core.genes.GeneImport;
import org.uw.nwgc.multiomics.core.genes.GeneService;
import org.uw.nwgc.multiomics.core.tag.TagService;
import org.uw.nwgc.multiomics.core.tag.ontology.ImportHumanPhenotypeOntology;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class StartupRunner {

    private final TagService tagService;
    private final GeneService geneService;

    public StartupRunner(
            TagService tagService,
            GeneService geneService
    ) {
        this.tagService = tagService;
        this.geneService = geneService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        System.out.println("Application started, tables initialized, repositories ready.");

        populateGenesTable();
        populateTissueTable();
    }

    private void populateTissueTable() {
        if (tagService.tissuesCount() != 0) {
            return;
        }

        try {
            File file = ResourceUtils.getFile("classpath:imports/uberon.zip");

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry entry = zis.getNextEntry();
                if (entry == null || entry.isDirectory()) {
                    zis.close();
                    throw new IOException("No file entry found in the zip archive.");
                }

                List<Tissue> tissues = ImportHumanPhenotypeOntology.importData(zis);
                tagService.saveTissues(tissues);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateGenesTable() {
        if (geneService.count() != 0) {
            return;
        }

        try {
            File file = ResourceUtils.getFile("classpath:imports/hgnc_complete_set.zip");

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry entry = zis.getNextEntry();
                if (entry == null || entry.isDirectory()) {
                    zis.close();
                    throw new IOException("No file entry found in the zip archive.");
                }

                ObjectMapper mapper = new ObjectMapper()
                        .registerModules();

                GeneImport.GeneListJsonRoot geneListJsonRoot = mapper.readValue(zis, GeneImport.GeneListJsonRoot.class);

                List<Gene> genes = new ArrayList<>();
                for (GeneImport.Doc doc : geneListJsonRoot.response.docs) {
                    genes.add(new Gene(doc));
                }

                geneService.saveAll(genes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}