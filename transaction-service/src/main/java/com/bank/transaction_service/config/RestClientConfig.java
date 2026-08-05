package com.bank.transaction_service.config;

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
    public RestClient accountRestClient(@Value("${account-service.url}") String accountServiceUrl) {
        return RestClient.builder()
                .baseUrl(accountServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    // Forward the caller's own JWT so account-service can apply
                    // its ownership rules to the real end user, rather than this
                    // service acting as an unidentified trusted caller.
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
