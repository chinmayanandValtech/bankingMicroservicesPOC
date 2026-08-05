package com.bank.customer_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Reads the identity of the caller out of the validated JWT.
 *
 * The "customerId" claim is put into the token by a Keycloak protocol mapper,
 * backed by a user attribute that only realm admins are allowed to edit — so a
 * customer cannot change their own customerId to impersonate someone else.
 * Admins have no customerId claim at all; they are bank staff, not customers.
 */
@Component
public class CurrentUser {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String CUSTOMER_ID_CLAIM = "customerId";

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLE_ADMIN::equals);
    }

    public Long getCustomerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        Object claim = jwt.getClaim(CUSTOMER_ID_CLAIM);
        if (claim == null) {
            return null;
        }
        if (claim instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(claim.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
