package com.tscore.client;

import com.tscore.dto.KeycloakUserRepresentation;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "keycloak-admin")
public interface KeycloakAdminClient {

    @POST
    @Path("/admin/realms/{realm}/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createUser(
            @PathParam("realm") String realm,
            @HeaderParam("Authorization") String authorizationHeader,
            KeycloakUserRepresentation userRepresentation
    );
}
