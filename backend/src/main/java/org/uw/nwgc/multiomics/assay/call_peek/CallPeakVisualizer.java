package org.uw.nwgc.multiomics.assay.call_peek;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.assay.differential_expression.DifferentialExpression;
import org.uw.nwgc.multiomics.core.resources.VizTransformer;
import org.uw.nwgc.multiomics.util.RgbColor;

import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class CallPeakVisualizer implements VizTransformer {

    @Override
    public List<String> visualizationComponents() {
        return List.of("CallPeak");
    }

    @Override
    public Object serveResource(Reader reader, HttpHeaders header) {
        try {
            return CallPeakParser.parse(IOUtils.toByteArray(reader));
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
