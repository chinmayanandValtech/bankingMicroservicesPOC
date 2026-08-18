package com.bank.api_gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TraceService {

    private final RestClient restClient;

    public TraceService(
            @Value("${OBSERVABILITY_ZIPKIN_BASE_URL:http://zipkin:9411}")
            String zipkinBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(zipkinBaseUrl)
                .build();
    }

    public JsonNode getTrace(String traceId) {

        return restClient.get()
                .uri("/api/v2/trace/{traceId}", traceId)
                .retrieve()
                .body(JsonNode.class);
    }
}