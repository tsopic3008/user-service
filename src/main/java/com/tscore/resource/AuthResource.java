package com.tscore.resource;

import com.tscore.client.MailingClient;
import com.tscore.dto.LoginRequest;
import com.tscore.dto.MailRequest;
import com.tscore.dto.RegisterRequest;
import com.tscore.dto.UserDTO;
import com.tscore.service.AuthService;
import com.tscore.service.KeycloakService;
import com.tscore.service.UserService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    KeycloakService keycloakService;

    @Inject
    UserService userService;

    @Inject
    @RestClient
    MailingClient mailingClient;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        Map<String, Object> token = authService.login(request);
        return Response.ok(token).build();
    }

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        keycloakService.registerUserInKeycloak(request);
        UserDTO created = userService.registerUser(request);
        sendWelcomeEmailAsync(request.email(), request.username());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    private void sendWelcomeEmailAsync(String email, String username) {
        try {
            mailingClient.sendWelcomeEmail(new MailRequest(email, username));
        } catch (Exception e) {
            Log.warnf("Welcome email could not be sent to '%s': %s", email, e.getMessage());
        }
    }
}
