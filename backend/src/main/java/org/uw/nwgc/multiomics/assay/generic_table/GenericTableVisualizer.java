package org.uw.nwgc.multiomics.assay.generic_table;

import org.springframework.http.ResponseEntity;
import org.uw.nwgc.multiomics.core.transformer.ServableResource;
import org.uw.nwgc.multiomics.core.transformer.VizTransformer;
import org.uw.nwgc.multiomics.util.FileUtil;
import org.uw.nwgc.multiomics.util.coordinates_reader.GenomicFileParser;
import org.uw.nwgc.multiomics.util.coordinates_reader.io.SpreadsheetReader;
import org.uw.nwgc.multiomics.util.coordinates_reader.model.ParsedGenomicDataDTO;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;

public class GenericTableVisualizer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("generic-table");
    }

    @Override
    public Object serveResource(ServableResource resource) throws IOException {
        for (SpreadsheetReader.Format fmt : SpreadsheetReader.Format.values()) {
            System.out.println("Trying to Read Format " + fmt.name());
            try (InputStream is = resource.streamSource().open(); GenomicFileParser parser = GenomicFileParser.fromStream(is, fmt)) {
                ParsedGenomicDataDTO pd = new ParsedGenomicDataDTO(parser.parseRecords());
                return ResponseEntity.ok(pd);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        return null;
    }

    @Override
    public boolean validateResource(Reader reader, String pathString) {
        Path resolvedPath = FileUtil.resolvePath(pathString);
        String lowerPath = resolvedPath.toString().toLowerCase();

        return lowerPath.endsWith(".xlsx") || lowerPath.endsWith(".xls") || lowerPath.endsWith(".csv") || lowerPath.endsWith(".tsv");
    }
}
