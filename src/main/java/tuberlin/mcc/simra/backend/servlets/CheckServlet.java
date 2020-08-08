package tuberlin.mcc.simra.backend.servlets;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.getHashes;
import static tuberlin.mcc.simra.backend.control.Util.getBaseFolderPath;
import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;
import static tuberlin.mcc.simra.backend.control.Util.getRegions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ####################################################
// WARNING!
// This file should not be used anymore. 
// It should be deleted as soon as guaranteed that no old app version is using this endoint 
// Please modify it in the according api version folder!
// ####################################################

@SuppressWarnings("Duplicates")
@Path("check")
public class CheckServlet {

    private static Logger logger = LoggerFactory.getLogger(CheckServlet.class.getName());
    private static String sp = File.separator;
    private static int INTERFACE_VERSION = 10;

    @GET
    @Path("regions")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkRegions(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("regions: " + INTERFACE_VERSION + " clientHash: " + clientHash + " serverHash: " + serverHash
                + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash))) {
            return Response.status(400, "not authorized").build();
        }
        String sp = File.separator;

        String regions = getRegions(getBaseFolderPath() + sp + "simRa_regions.config");
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
        logger.info("version: " + INTERFACE_VERSION + " clientHash: " + clientHash + " serverHash: " + serverHash
                + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash))) {
            return Response.status(400, "not authorized").build();
        }
        String sp = File.separator;

        String[] responseArray = getConfigValues(new String[] { "critical", "newestAppVersion", "urlToNewestAPK" },
                getBaseFolderPath() + sp + "simRa_backend.config");
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