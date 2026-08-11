package com.bank.financialintelligence.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * transaction-service requires a token of its own, so Feign calls must carry
 * one or they come back 401. Forwarding the caller's JWT (rather than using a
 * service account) keeps the end user's identity intact across the hop, so
 * transaction-service applies its ownership rules to the real user — a customer
 * asking for insights still only ever sees their own accounts.
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor bearerTokenRelayInterceptor() {
        return template -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue());
            }
        };
    }
}
