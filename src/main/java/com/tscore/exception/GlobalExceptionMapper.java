package com.tscore.exception;

import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof RegistrationException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }

        if (exception instanceof WebApplicationException e) {
            return Response.status(e.getResponse().getStatus())
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }

        Log.errorf(exception, "Unhandled exception: %s", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("An unexpected error occurred"))
                .build();
    }
}
