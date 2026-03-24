package com.tscore.client;

import com.tscore.dto.MailRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/mail")
@RegisterRestClient(configKey = "mailing-service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MailingClient {

    @POST
    Response sendWelcomeEmail(MailRequest mailRequest);
}
