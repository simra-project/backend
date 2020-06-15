package tuberlin.mcc.simra.backend.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Paths;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.getHashes;
import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;
import static tuberlin.mcc.simra.backend.control.Util.getRegions;

@SuppressWarnings("Duplicates")
@Path("check")
public class CheckServlet {

    private static Logger logger = LoggerFactory.getLogger(CheckServlet.class.getName());
    private static String sp = File.separator;
    private static java.nio.file.Path currentRelativePath = Paths.get("");
    private static String absolutePath = currentRelativePath.toAbsolutePath().toString();
    private static int INTERFACE_VERSION = 10;
    @GET
    @Path("regions")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkRegions(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("regions: " + INTERFACE_VERSION + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash))&&(!serverHash2.equals(clientHash))&&(!("0"+serverHash).equals(clientHash))&&(!("0"+serverHash2).equals(clientHash))){
            return Response.status(400, "not authorized").build();
        }
        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String sp = File.separator;

        String regions = getRegions(absolutePath+sp+"simRa_regions.config" );
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

    @GET
    @Path("version")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkVersion(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("version: " + INTERFACE_VERSION + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash))&&(!serverHash2.equals(clientHash))&&(!("0"+serverHash).equals(clientHash))&&(!("0"+serverHash2).equals(clientHash))){
            return Response.status(400, "not authorized").build();
        }
        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String sp = File.separator;

        String[] responseArray = getConfigValues(new String[] {"critical","newestAppVersion","urlToNewestAPK"},absolutePath+sp+"simRa_backend.config" );
        if (responseArray != null && responseArray.length > 2) {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(responseArray[0] + "splitter" + responseArray[1] + "splitter" + responseArray[2]);
                    writer.flush();
                }
            };
            return Response.ok(stream).build();
        } else {
            return Response.status(404, "ERROR: config could not be read").build();
        }
    }

}