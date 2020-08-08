package tuberlin.mcc.simra.backend.servlets.version11;

import static tuberlin.mcc.simra.backend.control.Util.getBaseFolderPath;
import static tuberlin.mcc.simra.backend.control.Util.getRegions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuberlin.mcc.simra.backend.control.filter.Secured;

@SuppressWarnings("Duplicates")
@Path("11")
public class CheckServlet {

    private static Logger logger = LoggerFactory.getLogger(CheckServlet.class.getName());

    @GET
    @Path("check-regions")
    @Secured
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkRegions() {

        String regions = getRegions(getBaseFolderPath() + File.separator + "simRa_regions.config");
        if (regions.length() > 2) {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(regions);
                    writer.flush();
                }
            };
            return Response.ok(stream).build();
        } else {
            return Response.status(404, "ERROR: config could not be read").build();
        }
    }
}