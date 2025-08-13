package com.tscore.keycloak;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@RegisterRestClient(configKey = "keycloak-token")
public interface KeycloakTokenClient {

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ClientHeaderParam(name = "Content-Type", value = "application/x-www-form-urlencoded")
    Map<String, Object> getToken(@FormParam("grant_type") String grantType,
                                 @FormParam("client_id") String clientId,
                                 @FormParam("client_secret") String clientSecret);
}