package com.tscore.service;


import com.tscore.keycloak.KeycloakTokenClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@ApplicationScoped
public class KeycloakService {

    private static final String CLIENT_ID = "tscore";
    private static final String CLIENT_SECRET = "MpZD6wUPrDaWcZ4nD4HQyOst7jk0X7Zf";

    @Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    public String getServiceAccountToken() {
        Map<String, Object> token = tokenClient.getToken(
                "client_credentials",
                CLIENT_ID,
                CLIENT_SECRET
        );
        return token.get("access_token").toString();
    }
}

