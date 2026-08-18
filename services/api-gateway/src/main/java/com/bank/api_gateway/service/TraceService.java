package com.bank.api_gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TraceService {

    private final WebClient webClient;

    public TraceService(
            @Value("${OBSERVABILITY_ZIPKIN_BASE_URL:http://zipkin:9411}")
            String zipkinBaseUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(zipkinBaseUrl)
                .build();
    }

    public Mono<JsonNode> getRecentTraces(int limit) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v2/traces")
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<JsonNode> getTrace(String traceId) {

        return webClient.get()
                .uri("/api/v2/trace/{traceId}", traceId)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}