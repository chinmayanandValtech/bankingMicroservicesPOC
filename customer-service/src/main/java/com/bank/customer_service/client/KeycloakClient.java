package com.bank.customer_service.client;

import com.bank.customer_service.exception.KeycloakProvisioningException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakClient {

    private final RestClient keycloakRestClient;
    private final String realm;
    private final String adminUsername;
    private final String adminPassword;

    public KeycloakClient(RestClient keycloakRestClient,
                           @Value("${keycloak.realm}") String realm,
                           @Value("${keycloak.admin.username}") String adminUsername,
                           @Value("${keycloak.admin.password}") String adminPassword) {
        this.keycloakRestClient = keycloakRestClient;
        this.realm = realm;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    // Creates a login account in Keycloak for a newly registered customer,
    // and assigns the CUSTOMER role. Throws on any failure so the caller can
    // roll back the customer record it just created (see CustomerServiceImpl).
    public void createCustomerUser(String email, String firstName, String lastName, String password, Long customerId) {
        try {
            String adminToken = getAdminToken();
            String userId = createUser(adminToken, email, firstName, lastName, password, customerId);
            assignCustomerRole(adminToken, userId);
        } catch (KeycloakProvisioningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new KeycloakProvisioningException("Failed to create login account for customer: " + ex.getMessage(), ex);
        }
    }

    private String getAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        Map<String, Object> response = keycloakRestClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("access_token") == null) {
            throw new KeycloakProvisioningException("Could not authenticate with Keycloak admin API");
        }
        return (String) response.get("access_token");
    }

    private String createUser(String adminToken, String email, String firstName, String lastName,
                               String password, Long customerId) {
        Map<String, Object> body = Map.of(
                "username", email,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", true,
                // Stamped into the access token by the "customerId" protocol
                // mapper, and used by every service to decide which records
                // this user may touch. Only realm admins can edit it.
                "attributes", Map.of("customerId", List.of(String.valueOf(customerId))),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                ))
        );

        var response = keycloakRestClient.post()
                .uri("/admin/realms/{realm}/users", realm)
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    if (res.getStatusCode().value() == 409) {
                        throw new KeycloakProvisioningException("A login account already exists for email: " + email);
                    }
                    throw new KeycloakProvisioningException("Keycloak user creation failed with status " + res.getStatusCode());
                })
                .toBodilessEntity();

        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new KeycloakProvisioningException("Keycloak did not return the new user's location");
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @SuppressWarnings("unchecked")
    private void assignCustomerRole(String adminToken, String userId) {
        Map<String, Object> role = keycloakRestClient.get()
                .uri("/admin/realms/{realm}/roles/CUSTOMER", realm)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .body(Map.class);

        keycloakRestClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                .header("Authorization", "Bearer " + adminToken)
                .body(List.of(role))
                .retrieve()
                .toBodilessEntity();
    }
}
