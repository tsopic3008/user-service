package com.tscore.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("/secure")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SecureResource {

    @GET
    @RolesAllowed("user")
    public Response securedEndpoint(@Context SecurityContext securityContext) {
        String principal = securityContext.getUserPrincipal() != null
                ? securityContext.getUserPrincipal().getName()
                : "unknown";
        return Response.ok(Map.of("message", "Hello, " + principal + "!")).build();
    }
}
