package com.bank.api_gateway.dto;


public record ServiceHealthResponse(
        String name,
        String status,
        long responseTimeMs
) {
}
