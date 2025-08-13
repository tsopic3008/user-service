package com.tscore.service;


import com.tscore.keycloak.KeycloakTokenClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@ApplicationScoped
@Slf4j
public class KeycloakService {

    @ConfigProperty(name = "keycloak.client.secret")
    String clientSecret;

    @ConfigProperty(name = "keycloak.client.id")
    String clientId;

    @Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    @Inject
    @ConfigProperty(name = "quarkus.rest-client.keycloak-token.url")
    String keycloakTokenUrl;

    public String getServiceAccountToken() {
        log.info(">>> Pokušavam dohvatiti token sa URL-a: {}" , keycloakTokenUrl);
        System.out.println(">>> Pokušavam dohvatiti token sa URL-a: " + keycloakTokenUrl);

        Map<String, Object> token = tokenClient.getToken(
                "client_credentials",
                clientId,
                clientSecret
        );
        return token.get("access_token").toString();
    }
}

