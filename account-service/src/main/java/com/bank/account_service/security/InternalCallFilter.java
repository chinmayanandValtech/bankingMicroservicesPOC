package com.bank.account_service.security;

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
 * Money only moves through transaction-service, so that every movement lands in
 * the ledger. These endpoints still exist here (transaction-service calls them),
 * but they are not for direct use: a client hitting them straight would change a
 * balance with no transaction record behind it.
 *
 * The gateway strips this header from anything arriving from outside, so a
 * client cannot forge it. The shared secret is enough for a local POC; a real
 * deployment would use mTLS or a service-to-service token instead.
 */
@Component
public class InternalCallFilter extends OncePerRequestFilter {

    public static final String INTERNAL_CALL_HEADER = "X-Internal-Call";

    private final String expectedSecret;

    public InternalCallFilter(@Value("${internal.call.secret}") String expectedSecret) {
        this.expectedSecret = expectedSecret;
    }

    private boolean isMoneyMovement(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return path.equals("/api/accounts/transfer")
                || (path.startsWith("/api/accounts/") && path.endsWith("/deposit"))
                || (path.startsWith("/api/accounts/") && path.endsWith("/withdraw"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isMoneyMovement(request) && !expectedSecret.equals(request.getHeader(INTERNAL_CALL_HEADER))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"timestamp":"%s","status":403,"error":"Forbidden",\
                    "message":"Use /api/transactions to move money so it is recorded in the ledger"}"""
                    .formatted(LocalDateTime.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
