package org.uw.nwgc.multiomics.assay.isoform;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ImportData {

//    private final IsoformService isoformService;
//
//    @Autowired
//    public ImportData(IsoformService isoformService) {
//        this.isoformService = isoformService;
//    }
//
//    @PostMapping("/import/expression_outlier")
//    public String expressionOutlier(@RequestParam MultipartFile file) throws IOException {
//        String filename = file.getOriginalFilename();
//        CsvMapper csvMapper = new CsvMapper();
//        CsvSchema schema = CsvSchema.emptySchema().withHeader();
//
//        MappingIterator<Isoform> it = csvMapper.readerFor(Isoform.class).with(schema).readValues(file.getInputStream());
//        List<Isoform> csvList = it.readAll();
//
//        csvList.forEach(isoformService::saveIsoform);
//
//        return "Hello World";
//    }
//
//    @GetMapping(
//            value = "/assay/isoform/{id}",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<Isoform> getIsoformsById(@PathVariable int id) {
//        Isoform isoforms = isoformService.findById(id);
//        if (isoforms == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(isoforms);
//    }
//
//    @GetMapping("/assay/isoform/all")
//    public ResponseEntity<List<Isoform>> getAllIsoforms() {
//        List<Isoform> isoforms = isoformService.findAll();
//        if (isoforms.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(isoforms);
//    }
//
//    @GetMapping("/assay/isoform/cutoff/{cutoff}")
//    public ResponseEntity<List<Isoform>> getIsoformsByCutoff(@PathVariable Double cutoff) {
//        List<Isoform> isoforms = isoformService.findByCutoff(cutoff);
//        if (isoforms.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(isoforms);
//    }

}
