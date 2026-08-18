package com.bank.api_gateway.service;

import com.bank.api_gateway.dto.HealthResponse;
import com.bank.api_gateway.dto.ServiceHealthResponse;
import com.bank.api_gateway.dto.SystemHealthResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SystemHealthService {

    private final WebClient.Builder webClientBuilder;

    public SystemHealthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<SystemHealthResponse> getSystemHealth() {

        Mono<ServiceHealthResponse> accountService =
                checkService(
                        "Account Service",
                        "http://ACCOUNT-SERVICE"
                );

        Mono<ServiceHealthResponse> customerService =
                checkService(
                        "Customer Service",
                        "http://CUSTOMER-SERVICE"
                );

        Mono<ServiceHealthResponse> transactionService =
                checkService(
                        "Transaction Service",
                        "http://TRANSACTION-SERVICE"
                );

        Mono<ServiceHealthResponse> authService =
                checkService(
                        "Auth Service",
                        "http://AUTH-SERVICE"
                );

        return Mono.zip(
                accountService,
                customerService,
                transactionService,
                authService
        ).map(tuple -> {

            List<ServiceHealthResponse> services = List.of(
                    tuple.getT1(),
                    tuple.getT2(),
                    tuple.getT3(),
                    tuple.getT4()
            );

            return new SystemHealthResponse(
                    calculateOverallStatus(services),
                    Instant.now(),
                    services
            );
        });
    }

    private Mono<ServiceHealthResponse> checkService(
            String serviceName,
            String serviceUrl
    ) {

        long start = System.nanoTime();

        return webClientBuilder
                .build()
                .get()
                .uri(serviceUrl + "/actuator/health")
                .retrieve()
                .bodyToMono(HealthResponse.class)
                .timeout(Duration.ofSeconds(3))
                .map(response -> {

                    long duration = Duration
                            .ofNanos(System.nanoTime() - start)
                            .toMillis();

                    return new ServiceHealthResponse(
                            serviceName,
                            response != null
                                    ? response.status()
                                    : "UNKNOWN",
                            duration
                    );
                })
                .onErrorResume(exception -> {

                    long duration = Duration
                            .ofNanos(System.nanoTime() - start)
                            .toMillis();

                    System.err.println(
                            "Health check failed for "
                                    + serviceName
                                    + " using "
                                    + serviceUrl
                                    + ": "
                                    + exception.getClass().getName()
                                    + " - "
                                    + exception.getMessage()
                    );

                    return Mono.just(
                            new ServiceHealthResponse(
                                    serviceName,
                                    "DOWN",
                                    duration
                            )
                    );
                });
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