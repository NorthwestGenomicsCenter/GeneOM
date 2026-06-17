package org.uw.nwgc.multiomics.service;

import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.snapshot.AnalysisPositionSnapshot;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BedExportService {

    public byte[] generateBedFile(List<AnalysisPositionSnapshot> positions) {
        StringBuilder bedContent = new StringBuilder();
        for (AnalysisPositionSnapshot snapshot : positions) {
            String startString = snapshot.getStart();
            String endString = snapshot.getEnd();

            int start = (int) Math.round(Double.parseDouble(startString));
            int end = (int) Math.round(Double.parseDouble(endString));

            bedContent.append(snapshot.getChromosome())
                    .append("\t")
                    .append(start)
                    .append("\t")
                    .append(end);

            if (snapshot.getNotes() != null && !snapshot.getNotes().isBlank()) {
                bedContent.append("\t").append(snapshot.getNotes());
            }

            bedContent.append("\n");
        }
        return bedContent.toString().getBytes(StandardCharsets.UTF_8);
    }
}