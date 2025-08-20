package org.uw.nwgc.multiomics.core.analysis;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LayoutUpdatePublisher {
    public record AnalysisSSEUpdateDTO(LocalDateTime updateTime, String message) {};

    private final ConcurrentHashMap<String, LocalDateTime> lastUpdateTime =
            new ConcurrentHashMap<>();

    // multicasts to all current subscribers; buffers if a subscriber is slow
    private final ConcurrentHashMap<String, Sinks.Many<String>> sinks =
            new ConcurrentHashMap<>();

    public void publish(String id, AnalysisSSEUpdateDTO event) {
        var updateTime = lastUpdateTime.computeIfAbsent(id, (k) -> null);
        if (updateTime == null || updateTime.isBefore(event.updateTime)) {
            lastUpdateTime.put(id, event.updateTime);

            var sink = sinks.computeIfAbsent(id, (k) -> Sinks.many().multicast().onBackpressureBuffer());
            var result = sink.tryEmitNext(event.message);
            if (result.isFailure()) {
                // log if you care: backpressure/terminated/no-subscriber, etc.
            }
        }

    }

    public Flux<String> flux(String id) {
        return sinks.computeIfAbsent(id, (k) -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }
}
