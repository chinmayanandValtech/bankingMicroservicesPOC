package com.bank.customer_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient keycloakRestClient(@Value("${keycloak.url}") String keycloakUrl) {
        return RestClient.builder()
                .baseUrl(keycloakUrl)
                .build();
    }
}
