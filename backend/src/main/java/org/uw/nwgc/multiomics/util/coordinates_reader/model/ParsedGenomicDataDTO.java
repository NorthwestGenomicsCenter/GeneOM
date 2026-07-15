package org.uw.nwgc.multiomics.util.coordinates_reader.model;

import lombok.Data;

import java.util.*;

@Data
public class ParsedGenomicDataDTO {
    private String displayName;
    private List<String> columnHeaders;
    private Map<String, String> dataTypes;
    private Map<String, Object> columnFilters;
    private List<Map<String, Object>> rawValues;

    public ParsedGenomicDataDTO(String displayName, List<GenomicRecord> records) {
        this.displayName = displayName;
        init(records);
    }

    private void init(List<GenomicRecord> records) {
        if (records == null || records.isEmpty()) {
            this.columnHeaders = List.of();
            this.dataTypes = Map.of();
            this.columnFilters = Map.of();
            return;
        }

        // Column headers from rawValues keys of first record
        this.columnHeaders = List.copyOf(records.getFirst().getRawValues().keySet());

        // Infer data types and build filters
        this.dataTypes = new LinkedHashMap<>();
        this.columnFilters = new LinkedHashMap<>();

        this.rawValues = records.stream()
                .map(record -> {
                    Map<String, Object> values = record.getRawValues();
                    values.put("nav_genomic_coords", record.getCoordinate().orElse(null));
                    return values;
                })
//                .limit(500)
                .toList();

        for (String column : columnHeaders) {
            List<String> values = records.stream()
                    .map(r -> r.getRawValues().get(column))
                    .filter(v -> v instanceof String && !((String) v).isBlank())
                    .map(v -> (String) v)
                    .toList();

            if (isNumericColumn(values)) {
                dataTypes.put(column, "numeric");

                DoubleSummaryStatistics stats = values.stream()
                        .mapToDouble(Double::parseDouble)
                        .summaryStatistics();

                columnFilters.put(column, Map.of(
                        "min", stats.getMin(),
                        "max", stats.getMax(),
                        "filterType", "numeric"
                ));
            } else {
                dataTypes.put(column, "string");

                Set<String> unique = new LinkedHashSet<>(values);
                if (unique.size() < 500) {
                    columnFilters.put(column, Map.of(
                            "values", unique,
                            "filterType", "discrete"
                    ));
                } else {
                    columnFilters.put(column, Map.of("filterType", "continuous"));
                }
            }
        }
    }

    private boolean isNumericColumn(List<String> values) {
        if (values.isEmpty()) return false;
        return values.stream().allMatch(v -> {
            try {
                Double.parseDouble(v);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }
}
