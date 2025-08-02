package com.tscore.endpoint;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthEndpoint {

    @GET
    public Response health() {
        return Response.ok()
                .entity("{\"status\": \"UP\", \"service\": \"tscore-app\"}")
                .build();
    }
} 