package com.bank.api_gateway.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Backs the "readiness" health group: the gateway should not receive traffic
 * until every service it routes to has actually registered with Eureka,
 * otherwise lb:// routes resolve to zero instances right after a fresh boot.
 */
@Component("downstreamServices")
public class DownstreamServicesHealthIndicator implements HealthIndicator {

    private static final List<String> REQUIRED_SERVICES = List.of(
            "ACCOUNT-SERVICE",
            "CUSTOMER-SERVICE",
            "TRANSACTION-SERVICE",
            "AUTH-SERVICE"
    );

    private final DiscoveryClient discoveryClient;

    public DownstreamServicesHealthIndicator(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Health health() {
        Map<String, Integer> instanceCounts = new LinkedHashMap<>();
        for (String serviceId : REQUIRED_SERVICES) {
            instanceCounts.put(serviceId, instanceCount(serviceId));
        }

        boolean allRegistered = instanceCounts.values().stream().allMatch(count -> count > 0);

        return (allRegistered ? Health.up() : Health.down())
                .withDetails(instanceCounts)
                .build();
    }

    private int instanceCount(String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        return instances == null ? 0 : instances.size();
    }
}
