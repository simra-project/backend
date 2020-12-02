package tuberlin.mcc.simra.backend.servlets;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.isAuthorized;
import static tuberlin.mcc.simra.backend.control.Util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@SuppressWarnings("Duplicates")
@Path("check")
public class CheckServlet {

    private static int INTERFACE_VERSION = 10;
    private static String sp = File.separator;

    @GET
    @Path("regions")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkRegions(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,"checkRegions")) {
            return Response.status(400, "not authorized").build();
        }

        String regions = getContentOfTextFile(getBaseFolderPath() + sp + "simRa_regions.config");
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
    @Path("news")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN })
    public Response checkNews(@QueryParam("clientHash") @DefaultValue("10") String clientHash, @QueryParam("lastSeenNewsID") @DefaultValue("10") int lastSeenNewsID, @QueryParam("newsLanguage") @DefaultValue("en")String newsLanguage) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,"checkNews_" + newsLanguage)) {
            return Response.status(400, "not authorized").build();
        }

        String newsPath = getBaseFolderPath() + sp + "simRa_news_" + newsLanguage + ".config";
        String news = getContentOfTextFile(newsPath);
        if (news.length() > 2) {
            if (Integer.parseInt(news.split(System.lineSeparator())[0].replace("#","")) > lastSeenNewsID) {
                StreamingOutput stream = new StreamingOutput() {
                    @Override
                    public void write(OutputStream os) throws IOException, WebApplicationException {
                        Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write(news);
                        writer.flush();
                    }
                };
                return Response.ok(stream).build();
            } else {
                return Response.ok().build();
            }
        } else {
            return Response.status(404, "ERROR: config could not be read").build();
        }
    }

    @GET
    @Path("news_de")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkNewsDE(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,"checkNews_de")) {
            return Response.status(400, "not authorized").build();
        }

        String news = getContentOfTextFile(getBaseFolderPath() + sp + "simRa_news_de.config");
        if (news.length() > 2) {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(news);
                    writer.flush();
                }
            };
            return Response.ok(stream).build();
        } else {
            return Response.status(404, "ERROR: config could not be read").build();
        }
    }

    @GET
    @Path("news_en")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkNewsEN(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,"checkNews_en")) {
            return Response.status(400, "not authorized").build();
        }

        String news = getContentOfTextFile(getBaseFolderPath() + sp + "simRa_news_en.config");
        if (news.length() > 2) {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(news);
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

        if (!isAuthorized(clientHash,INTERFACE_VERSION,"checkVersion")) {
            return Response.status(400, "not authorized").build();
        }

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