package tuberlin.mcc.simra.backend.servlets.version11;

import static java.lang.System.currentTimeMillis;
import static tuberlin.mcc.simra.backend.control.FileListController.*;
import static tuberlin.mcc.simra.backend.control.SimRauthenticator.isAuthorized;
import static tuberlin.mcc.simra.backend.control.Util.*;

import java.io.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuberlin.mcc.simra.backend.control.FileListController;

@SuppressWarnings("Duplicates")
@Path("11")
public class UploadServlet {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());
    private static String sp = File.separator;
    private static int INTERFACE_VERSION = 11;

    @POST
    @Path("ride")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadRide(@QueryParam("loc") @DefaultValue("de") String loc,
                               @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,loc)) {
            return Response.status(400, "not authorized").build();
        }

        String fileBody = content.substring(content.indexOf(System.lineSeparator()) + 1);
        String key = generateRideKey(fileBody);
        String password = RandomStringUtils.randomAlphanumeric(10);
        String directory = getBaseFolderPath() + sp + "SimRa" + sp + loc + sp + "Rides";
        updateKeyValue(key, password, getBaseFolderPath() + sp + "fileList.csv");
        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        overWriteContentToFile(directory + sp + key, content);
        logger.info("writing to: " + directory + sp + key);
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(key + "," + password);
                writer.flush();
            }
        };
        return Response.ok(stream).build();
    }

    @PUT
    @Path("ride")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN,
            MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateRide(@QueryParam("fileHash") String fileHash, @QueryParam("filePassword") String filePassword,
            @QueryParam("loc") @DefaultValue("Berlin") String loc,
                               @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,loc)) {
            return Response.status(400, "not authorized").build();
        }

        String directory = "SimRa" + sp + loc + sp + "Rides";

        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        logger.info("writing to filePath: " + getBaseFolderPath() + sp + directory + sp + fileHash);
        boolean success = overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + fileHash, content);
        if (success) {
            return Response.status(200, "OK").build();
        } else {
            return Response.status(500, "OK").build();
        }

    }

    @POST
    @Path("profile")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadProfile(@QueryParam("loc") @DefaultValue("de") String loc,
                                  @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,loc)) {
            return Response.status(400, "not authorized").build();
        }

        String hash = generateProfileKey();
        String password = RandomStringUtils.randomAlphanumeric(10);

        String directory = "SimRa" + sp + loc + sp + "Profiles";
        FileListController.updateKeyValue(hash, password, getBaseFolderPath() + sp + "fileList.csv");

        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + hash, content);

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(hash + "," + password);
                writer.flush();
            }
        };
        return Response.ok(stream).build();
    }

    @PUT
    @Path("profile")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN,
            MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProfile(@QueryParam("fileHash") String fileHash,
            @QueryParam("filePassword") String filePassword, @QueryParam("loc") @DefaultValue("Berlin") String loc,
                                  @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {
        if (!isAuthorized(clientHash,INTERFACE_VERSION,loc)) {
            return Response.status(400, "not authorized").build();
        }

        String directory = "SimRa" + sp + loc + sp + "Profiles";
        fileHash = fileHash.replace("profile.csv", "");

        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        logger.info("writing to filePath: " + getBaseFolderPath() + sp + directory + sp + fileHash);
        boolean success = overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + fileHash, content);

        if (success) {
            return Response.status(200, "OK").build();
        } else {
            return Response.status(500, "Error").build();
        }
    }

    @POST
    @Path("crash")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadCrashLog(@QueryParam("loc") @DefaultValue("de") String loc,
                                   @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,loc)) {
            return Response.status(400, "not authorized").build();
        }

        String ts;
        try {
            ts = content.split("\n")[1].split(": ")[1];
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            ts = String.valueOf(currentTimeMillis());
            e.printStackTrace();
        }
        String directory = "SimRa" + sp + loc + sp + "CRASH";

        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        boolean success = overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + ts, content);
        if (success) {
            return Response.status(200, "OK").build();
        } else {
            return Response.status(500, "Error").build();
        }
    }

    @PUT
    @Path("update")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN,
            MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePut(@QueryParam("fileHash") String fileHash, @QueryParam("filePassword") String filePassword,
            @QueryParam("loc") @DefaultValue("Berlin") String loc,
                              @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,loc)) {
            return Response.status(400, "not authorized").build();
        }

        return overWriteAndReturnStatus(fileHash, String.valueOf(INTERFACE_VERSION), loc, content);

    }

    @POST
    @Path("debug")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadDebug(@QueryParam("clientHash") @DefaultValue("10") String clientHash,
                                @FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

        if (!isAuthorized(clientHash,INTERFACE_VERSION,"de")) {
            return Response.status(400, "not authorized").build();
        }

        String directory = "Debug";
        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        try {
            OutputStream out = new FileOutputStream(new File(directory + sp + RandomStringUtils.randomAlphanumeric(10) + ".zip"));
            int read;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            return Response.status(200, "OK").build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(500, "Error").build();
        }
    }

    // fileHash: filename, content: content of the file
    private Response overWriteAndReturnStatus(String fileHash, String version, String loc, String content) {
        String sp = File.separator;

        String directory;

        if (fileHash.contains("profile.csv")) {
            directory = version + sp + loc + sp + "Profiles";
            fileHash = fileHash.replace("profile.csv", "");
        } else {
            directory = version + sp + loc + sp + "Rides";
        }
        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        logger.info("writing to filePath: " + getBaseFolderPath() + sp + directory + sp + fileHash);
        boolean success = overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + fileHash, content);
        if (success) {
            return Response.status(200, "OK").build();
        } else {
            return Response.status(500, "Error").build();
        }
    }
}