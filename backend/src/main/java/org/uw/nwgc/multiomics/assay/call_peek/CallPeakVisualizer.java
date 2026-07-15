package org.uw.nwgc.multiomics.assay.call_peek;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.transformer.ServableResource;
import org.uw.nwgc.multiomics.core.transformer.VizTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

@Component
public class CallPeakVisualizer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("CallPeak");
    }

    @Override
    public Object serveResource(ServableResource servableResource) throws IOException {
        try (InputStream is = servableResource.streamSource().open()) {
            return CallPeakParser.parse(IOUtils.toByteArray(is));
        } catch (Exception ignored) {}

        return List.of();
    }

    @Override
    public boolean validateResource(Reader reader, String pathString) {
        if (
            !pathString.endsWith("xls") &&
            !pathString.endsWith("xlsx") &&
            !pathString.endsWith("csv")
        ) {
            return false;
        }

        try {
            byte[] bytes = IOUtils.toByteArray(reader);
            return !CallPeakParser.parse(bytes).isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
