package tuberlin.mcc.simra.backend.servlets;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Operation;

@Path("/openapi")
public class OpenApiServlet extends BaseOpenApiResource {
    @Context
    ServletConfig config;

    @Context
    Application app;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(hidden = true)
    public Response getOpenApi(@Context javax.ws.rs.core.HttpHeaders headers, @Context UriInfo uriInfo)
            throws Exception {

        return super.getOpenApi(headers, config, app, uriInfo, "json");
    }
}