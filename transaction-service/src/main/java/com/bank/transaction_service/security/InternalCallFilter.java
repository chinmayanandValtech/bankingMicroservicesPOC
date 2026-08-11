package com.bank.transaction_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Guards /api/transactions/internal/**, which lets another service write a
 * history record directly. That must never be reachable by a client, or anyone
 * could invent transactions that never happened.
 *
 * The gateway strips this header from inbound requests, so it cannot be forged
 * from outside. A shared secret is adequate for a local POC; production would
 * use mTLS or a proper service-to-service token.
 */
@Component
public class InternalCallFilter extends OncePerRequestFilter {

    public static final String INTERNAL_CALL_HEADER = "X-Internal-Call";

    private final String expectedSecret;

    public InternalCallFilter(@Value("${internal.call.secret}") String expectedSecret) {
        this.expectedSecret = expectedSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean internalPath = request.getRequestURI().startsWith("/api/transactions/internal/");

        if (internalPath && !expectedSecret.equals(request.getHeader(INTERNAL_CALL_HEADER))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"timestamp":"%s","status":403,"error":"Forbidden",\
                    "message":"This endpoint is for internal service use only"}"""
                    .formatted(LocalDateTime.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
