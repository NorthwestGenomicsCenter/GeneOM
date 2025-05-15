package org.uw.nwgc.multiomics.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Compute {

    public record MinMax(
            @JsonProperty("min") Double min,
            @JsonProperty("max") Double max) {}

    public static <T> MinMax minMax(Collection<T> collection, Function<T, Double> func) {
        return collection
            .parallelStream()
            .map(el -> {
                try {
                    Double value = func.apply(el);
                    return value != null ? value : Double.NaN;
                } catch (Exception e) {
                    return Double.NaN;
                }
            })
            .collect(Collectors.teeing(
                    Collectors.minBy(Double::compare),
                    Collectors.maxBy(Double::compare),
                    (min, max) -> new MinMax(
                            min.orElse(Double.NaN),
                            max.orElse(Double.NaN)
                    )
            ));
    }
}
