package com.tscore.endpoint;


import com.tscore.keycloak.KeycloakAuthClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginEndpoint {
    @Inject
    @RestClient
    KeycloakAuthClient keycloakClient;

    static final String CLIENT_ID = "tscore";
    static final String CLIENT_SECRET = "MpZD6wUPrDaWcZ4nD4HQyOst7jk0X7Zf";

    @POST
    public Response login(LoginRequest request) {
        try {
            Map<String, Object> token = keycloakClient.getToken(
                    "password",
                    CLIENT_ID,
                    CLIENT_SECRET,
                    request.username,
                    request.password
            );
            return Response.ok(token).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Invalid credentials"))
                    .build();
        }
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}
