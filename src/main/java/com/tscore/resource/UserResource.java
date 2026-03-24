package com.tscore.resource;

import com.tscore.dto.UserDTO;
import com.tscore.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    public Response getById(@PathParam("id") Long id) {
        return userService.findById(id)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/me/{username}")
    @RolesAllowed("user")
    public Response getByUsername(@PathParam("username") String username) {
        return userService.findByUsername(username)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
