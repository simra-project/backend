package tuberlin.mcc.simra.backend.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("status")
public class StatusServlet {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStatus() {

        return Response.status(200, "everything all right").build();
    }
}
