package com.tscore.service;

import com.tscore.client.KeycloakAuthClient;
import com.tscore.dto.LoginRequest;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "quarkus.oidc.client-id", defaultValue = "tscore")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    @Inject
    @RestClient
    KeycloakAuthClient keycloakAuthClient;

    public Map<String, Object> login(LoginRequest request) {
        try {
            return keycloakAuthClient.getToken(
                    "password",
                    clientId,
                    clientSecret,
                    request.username(),
                    request.password()
            );
        } catch (WebApplicationException e) {
            Log.warnf("Login failed for user '%s': HTTP %d", request.username(), e.getResponse().getStatus());
            throw e;
        }
    }
}
