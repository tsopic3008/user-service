package com.tscore.service;

import com.tscore.client.KeycloakAdminClient;
import com.tscore.client.KeycloakTokenClient;
import com.tscore.dto.KeycloakUserRepresentation;
import com.tscore.dto.RegisterRequest;
import com.tscore.exception.RegistrationException;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class KeycloakService {

    @ConfigProperty(name = "keycloak.client.id")
    String clientId;

    @ConfigProperty(name = "keycloak.client.secret")
    String clientSecret;

    @ConfigProperty(name = "keycloak.realm", defaultValue = "tscore")
    String realm;

    @Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    @Inject
    @RestClient
    KeycloakAdminClient adminClient;

    public String getServiceAccountToken() {
        var tokenResponse = tokenClient.getToken("client_credentials", clientId, clientSecret);
        return tokenResponse.get("access_token").toString();
    }

    public void registerUserInKeycloak(RegisterRequest request) {
        String token = getServiceAccountToken();
        KeycloakUserRepresentation payload = KeycloakUserRepresentation.from(request);

        Response response = adminClient.createUser(realm, "Bearer " + token, payload);
        int status = response.getStatus();

        if (status == 201) {
            Log.infof("User '%s' created in Keycloak realm '%s'", request.username(), realm);
        } else if (status == 409) {
            throw new RegistrationException("User already exists in Keycloak: " + request.username());
        } else {
            String body = response.readEntity(String.class);
            Log.errorf("Keycloak returned %d: %s", status, body);
            throw new RegistrationException("Keycloak registration failed with status " + status);
        }
    }
}
