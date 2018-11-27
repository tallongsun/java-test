package com.dl.grpc.test.zipkin;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.HttpSpanCollector;

public class BraveUtil {
    public static Brave brave(String serviceName) {
        return new Brave.Builder(serviceName)
            .traceSampler(Sampler.ALWAYS_SAMPLE)
            .spanCollector(HttpSpanCollector.create(String.format("http://localhost:9411"),
                new EmptySpanCollectorMetricsHandler()))
            .build();
    }
}
