package com.bank.api_gateway.dto;

import java.time.Instant;
import java.util.List;

public record SystemHealthResponse(
        String status,
        Instant timestamp,
        List<ServiceHealthResponse> services
) {
}
