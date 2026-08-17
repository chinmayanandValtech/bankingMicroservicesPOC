package com.bank.api_gateway.service;

import com.bank.api_gateway.dto.HealthResponse;
import com.bank.api_gateway.dto.ServiceHealthResponse;
import com.bank.api_gateway.dto.SystemHealthResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemHealthService {

    private final RestClient restClient;

    public SystemHealthService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public SystemHealthResponse getSystemHealth() {

        List<ServiceHealthResponse> services = new ArrayList<>();

        services.add(checkService(
                "Account Service",
                "http://ACCOUNT-SERVICE"
        ));

        services.add(checkService(
                "Customer Service",
                "http://CUSTOMER-SERVICE"
        ));

        services.add(checkService(
                "Transaction Service",
                "http://TRANSACTION-SERVICE"
        ));

        services.add(checkService(
                "Auth Service",
                "http://AUTH-SERVICE"
        ));

        return new SystemHealthResponse(
                calculateOverallStatus(services),
                Instant.now(),
                services
        );
    }

    private ServiceHealthResponse checkService(
            String serviceName,
            String serviceUrl
    ) {

        long start = System.nanoTime();

        try {

            HealthResponse response = restClient.get()
                    .uri(serviceUrl + "/actuator/health")
                    .retrieve()
                    .body(HealthResponse.class);

            long duration = Duration
                    .ofNanos(System.nanoTime() - start)
                    .toMillis();

            String status = response != null
                    ? response.status()
                    : "UNKNOWN";

            return new ServiceHealthResponse(
                    serviceName,
                    status,
                    duration
            );

        } catch (Exception e) {

            long duration = Duration
                    .ofNanos(System.nanoTime() - start)
                    .toMillis();

            return new ServiceHealthResponse(
                    serviceName,
                    "DOWN",
                    duration
            );
        }
    }

    private String calculateOverallStatus(
            List<ServiceHealthResponse> services
    ) {

        long upCount = services.stream()
                .filter(service ->
                        "UP".equalsIgnoreCase(service.status()))
                .count();

        if (upCount == services.size()) {
            return "UP";
        }

        if (upCount == 0) {
            return "DOWN";
        }

        return "DEGRADED";
    }
}