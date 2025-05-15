package org.uw.nwgc.multiomics.core.genes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GeneImport {

    private final GeneService geneService;

    @Autowired
    public GeneImport(GeneService geneService) {
        this.geneService = geneService;
    }

    @PostMapping("/import/import_gene_json")
    public ResponseEntity<String> importJson(@RequestParam MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModules();

        GeneListJsonRoot geneListJsonRoot = mapper.readValue(file.getInputStream(), GeneListJsonRoot.class);

        List<Gene> genes = new ArrayList<>();
        for (Doc doc : geneListJsonRoot.response.docs) {
            genes.add(new Gene(doc));
        }

        geneService.saveAll(genes);

        return ResponseEntity.ok("Imported: " + genes.size());
    }

    public static class GeneListJsonRoot {
        public ResponseHeader responseHeader;
        public Response response;
    }

    public static class Doc {
        public List<Integer> gene_group_id;
        public String hgnc_id;
        public List<String> gene_group;
        public String symbol;
        public String locus_group;
        public List<String> mgd_id;
        public String locus_type;
        public String date_modified;
        public List<String> rgd_id;
        public String uuid;
        public String entrez_id;
        public String merops;
        public String ucsc_id;
        public String vega_id;
        public String date_approved_reserved;
        public String name;
        public List<String> mane_select;
        public List<String> omim_id;
        public String status;
        public List<String> uniprot_ids;
        public String agr;
        public List<String> ccds_id;
        public List<String> refseq_accession;
        public String ensembl_gene_id;
        public String location;
        public ArrayList<Integer> pubmed_id;
        public List<String> prev_symbol;
        public List<String> rna_central_id;
        public List<String> prev_name;
        public List<String> ena;
        public List<String> alias_symbol;
        public List<String> alias_name;
        public List<String> lsdb;
        public List<String> enzyme_id;
        public List<String> curator_notes;
        public String lncipedia;
        public String date_symbol_changed;
        public String date_name_changed;
        public String gencc;
        public String orphanet;
        @JsonProperty("pseudogene.org")
        public String pseudogene_org;
        public String iuphar;
        public String cd;
        public String cosmic;
        public String lncrnadb;
        public Integer homeodb;
        public String bioparadigms_slc;
        public String imgt;
        public String mirbase;
        @JsonProperty("mamit-trnadb")
        public String mamit_trnadb;
        public String horde_id;
        public String snornabase;
        public String gtrnadb;
    }

    public static class Response {
        public int start;
        public ArrayList<Doc> docs;
        public int numFound;
        public boolean numFoundExact;
    }

    public static class ResponseHeader {
        public int status;
        @JsonProperty("QTime")
        public int qTime;
    }
}