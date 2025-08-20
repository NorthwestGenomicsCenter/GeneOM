package org.uw.nwgc.multiomics.assay;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;

import java.io.Reader;
import java.nio.file.Paths;
import java.util.List;

@Component
public class HiC implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("HiC");
    }

    @Override
    public Object serveResource(Reader reader, HttpHeaders header) {
        try (HdfFile hdfFile = new HdfFile(Paths.get("/temp/hic/P6_r2.hic"))) {
            Dataset dataset = hdfFile.getDatasetByPath("/");
            // data will be a Java array with the dimensions of the HDF5 dataset
            Object data = dataset.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Hello";
    }

    @Override
    public boolean validateResource(Reader reader) {
        return false;
    }
}
