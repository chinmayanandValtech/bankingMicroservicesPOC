package com.bank.account_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient customerServiceRestClient(
            @Value("${customer-service.url}") String customerServiceUrl) {
        return RestClient.builder()
                .baseUrl(customerServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    // customer-service now requires a token too, so forward the
                    // caller's own JWT rather than calling anonymously. This also
                    // keeps the end user's identity intact across the hop, so
                    // customer-service can apply the same ownership rules.
                    String token = currentTokenValue();
                    if (token != null) {
                        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    private String currentTokenValue() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return null;
    }
}
