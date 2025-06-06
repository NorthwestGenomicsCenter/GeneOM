package org.uw.nwgc.multiomics.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class ViewNameRegistry {

    private final Set<String> validViewNames;

    public ViewNameRegistry() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/viewNames.json");
        this.validViewNames = new HashSet<>(Arrays.asList(objectMapper.readValue(inputStream, String[].class)));
    }

    public boolean isValidView(String viewName) {
        return validViewNames.contains(viewName);
    }
}
