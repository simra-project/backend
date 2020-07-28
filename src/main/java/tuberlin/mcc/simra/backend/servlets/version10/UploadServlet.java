package tuberlin.mcc.simra.backend.servlets.version10;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.control.FileListController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.System.currentTimeMillis;
import static tuberlin.mcc.simra.backend.control.FileListController.checkKeyValue;
import static tuberlin.mcc.simra.backend.control.SimRauthenticator.getHashes;
import static tuberlin.mcc.simra.backend.control.Util.*;
import static tuberlin.mcc.simra.backend.control.Util.getBaseFolderPath;

@SuppressWarnings("Duplicates")
@Path("10")
public class UploadServlet {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());
    private static String sp = File.separator;
    private static int INTERFACE_VERSION = 10;

    @POST
    @Path("ride")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadRide(@QueryParam("loc") @DefaultValue("de") String loc,
            @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("ride upload version: " + INTERFACE_VERSION + " loc: " + loc + " clientHash: " + clientHash
                + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash))) {
            return Response.status(400, "not authorized").build();
        }

        logger.info(content);

        String fileBody = content.substring(content.indexOf(System.lineSeparator()) + 1);
        // TODO: should use random UUID instead UUID.randomUUID().toString();
        String hash = "VM2_" + fileBody.hashCode();

        // TODO: should use random UUID instead UUID.randomUUID().toString();
        String password = RandomStringUtils.randomAlphanumeric(10);

        String directory = getBaseFolderPath() + sp + "SimRa" + sp + loc + sp + "Rides";
        FileListController.updateKeyValue(hash, password, getBaseFolderPath() + sp + "fileList.csv");
        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        overWriteContentToFile(directory + sp + hash, content);

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
    @Path("ride")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN,
            MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateRide(@QueryParam("fileHash") String fileHash, @QueryParam("filePassword") String filePassword,
            @QueryParam("loc") @DefaultValue("Berlin") String loc,
            @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("fileHash: " + fileHash + " filePassword: " + filePassword + " version: " + INTERFACE_VERSION
                + " loc: " + loc + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: "
                + serverHash2);
        if (((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash)))
                || (!checkKeyValue(fileHash, filePassword))) {
            return Response.status(400, "not authorized").build();
        }

        String directory = "SimRa" + sp + loc + sp + "Rides";

        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("profile upload version: " + INTERFACE_VERSION + " loc: " + loc + " clientHash: " + clientHash
                + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash))) {
            return Response.status(400, "not authorized").build();
        }

        logger.info(content);

        String hash = "VM2_" + (RandomStringUtils.randomAlphanumeric(30)).hashCode();
        String password = RandomStringUtils.randomAlphanumeric(10);

        // if(!FileListController.containsKey(hash)) {
        String directory = "SimRa" + sp + loc + sp + "Profiles";
        FileListController.updateKeyValue(hash, password, getBaseFolderPath() + sp + "fileList.csv");

        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }/*
      * else { String errorID = RandomStringUtils.randomAlphanumeric(10) + ".txt";
      * String errorLogDirectory = "SimRa" + sp + "ErrorLogs" + sp + errorID; logger.
      * error("There was a ride post request with already existing hash as key. The log is saved under "
      * + errorLogDirectory); try {
      * Files.createDirectories(Paths.get(errorLogDirectory)); } catch (IOException
      * e) { e.printStackTrace(); } finally { StringBuilder errorContent = new
      * StringBuilder();
      * errorContent.append("loc: ").append(loc).append(System.lineSeparator())
      * .append("clientHash: ").append(clientHash).append(System.lineSeparator())
      * .append("serverHash: ").append(serverHash).append(System.lineSeparator())
      * .append("serverHash2: ").append(serverHash2).append(System.lineSeparator())
      * .append("profileContentHash: ").append(hash).append(System.lineSeparator())
      * .append("password: ").append(password).append(System.lineSeparator())
      * .append("content: ").append(System.lineSeparator()) .append(content);
      * 
      * overWriteContentToFile(errorLogDirectory , errorContent.toString()); }
      * 
      * return Response.status(404, "File already exists").build(); } }
      */

    @PUT
    @Path("profile")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN,
            MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProfile(@QueryParam("fileHash") String fileHash,
            @QueryParam("filePassword") String filePassword, @QueryParam("loc") @DefaultValue("Berlin") String loc,
            @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("fileHash: " + fileHash + " filePassword: " + filePassword + " version: " + INTERFACE_VERSION
                + " loc: " + loc + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: "
                + serverHash2);
        if (((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash)))
                || (!checkKeyValue(fileHash.replace("profile.csv", ""), filePassword))) {
            return Response.status(400, "not authorized").build();
        }

        String directory = "SimRa" + sp + loc + sp + "Profiles";
        fileHash = fileHash.replace("profile.csv", "");

        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("crash upload version: " + INTERFACE_VERSION + " loc: " + loc + " clientHash: " + clientHash
                + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash))) {
            return Response.status(400, "not authorized").build();
        }

        String ts = "";
        try {
            ts = content.split("\n")[1].split(": ")[1];
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            ts = String.valueOf(currentTimeMillis());
            e.printStackTrace();
        }
        String directory = "SimRa" + sp + loc + sp + "CRASH";
        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        String[] serverHashes = getHashes();
        String serverHash = serverHashes[0];
        String serverHash2 = serverHashes[1];
        logger.info("fileHash: " + fileHash + " filePassword: " + filePassword + " version: " + INTERFACE_VERSION
                + " loc: " + loc + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: "
                + serverHash2);
        if (((!serverHash.equals(clientHash)) && (!serverHash2.equals(clientHash))
                && (!("0" + serverHash).equals(clientHash)) && (!("0" + serverHash2).equals(clientHash)))
                || (!checkKeyValue(fileHash.replace("profile.csv", ""), filePassword))) {
            return Response.status(400, "not authorized").build();
        }

        logger.info(content);

        return overWriteAndReturnStatus(fileHash, String.valueOf(INTERFACE_VERSION), loc, content);

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
        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(getBaseFolderPath() + directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("writing to filePath: " + getBaseFolderPath() + sp + directory + sp + fileHash);
        boolean success = overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + fileHash, content);
        if (success) {
            return Response.status(200, "OK").build();
        } else {
            return Response.status(500, "Error").build();
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