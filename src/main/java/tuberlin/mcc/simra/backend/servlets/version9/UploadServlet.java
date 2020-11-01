package tuberlin.mcc.simra.backend.servlets.version9;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.control.FileListController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.isAuthorized;
import static tuberlin.mcc.simra.backend.control.Util.*;

@SuppressWarnings("Duplicates")
@Path("9")
public class UploadServlet {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());
    private static int INTERFACE_VERSION = 9;


    @POST
    @Path("upload")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadPost(@QueryParam("fileName") String fileName, @QueryParam("loc") @DefaultValue("de") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash, INTERFACE_VERSION, loc)) {
            return Response.status(400, "not authorized").build();
        }

        return writeAndReturnNamePassword(fileName, "9", loc, content);

    }

    // fileName: filename, content: content of the file
    private Response writeAndReturnNamePassword(String fileName, String version, String loc, String content){
        String sp = File.separator;
        String fileBody = content.substring(content.indexOf(System.lineSeparator())+1);
        String hash = "VM2_" + fileBody.hashCode();
        if(fileName.equals("profile.csv")){
           hash = "VM2_" + (RandomStringUtils.randomAlphanumeric(30)).hashCode();
        }
        String password = RandomStringUtils.randomAlphanumeric(10);

        if(!FileListController.containsKey(hash)){

            String directory;
            if (fileName.equals("profile.csv")) {
                directory = version + sp + loc + sp + "Profiles";
                FileListController.updateKeyValue(hash, password, getBaseFolderPath() + sp + "fileList.csv");
            } else if (fileName.startsWith("CRASH")) {
                String ts = "101";
                try {
                    ts = fileName.split("_")[1];
                    } catch (ArrayIndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
                directory = version + sp + loc + sp + "CRASH" + sp + ts;
            } else {
                directory = version + sp + loc + sp + "Rides";
                FileListController.updateKeyValue(hash, password, getBaseFolderPath() + sp + "fileList.csv");
            }
            if(!directoryAlreadyExists(directory)){
                try {
                    Files.createDirectories(Paths.get(directory));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileName.startsWith("CRASH")) {
                String[] fileNameLineArray = fileName.split("_");
                fileName = Arrays.toString(Arrays.copyOfRange(fileNameLineArray,2,(fileNameLineArray.length))).replace("[","").replace(",","").replace("]","");
                overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + fileName, content);
            } else {
                overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + hash, content);
            }

            return Response.status(200, hash + "," + password).build();
        }
        return Response.status(404, "File not found").build();
    }

    @PUT
    @Path("update")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePut(@QueryParam("fileHash") String fileHash, @QueryParam("filePassword") String filePassword, @QueryParam("loc") @DefaultValue("Berlin") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash, INTERFACE_VERSION, loc)) {
            return Response.status(400, "not authorized").build();
        }


        return overWriteAndReturnStatus(fileHash, "9", loc, content);

    }

    // fileHash: filename, content: content of the file
    private Response overWriteAndReturnStatus(String fileHash, String version, String loc, String content){
        String sp = File.separator;

        String directory;

        if (fileHash.contains("profile.csv")) {
            directory = version + sp + loc + sp + "Profiles";
            fileHash = fileHash.replace("profile.csv", "");
        } else {
            directory = version + sp + loc + sp + "Rides";
        }
        if(!directoryAlreadyExists(directory)){
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("writing to filePath: " + getBaseFolderPath() + sp + directory + sp + fileHash);
        overWriteContentToFile(getBaseFolderPath() + sp + directory + sp + fileHash, content);

        return Response.status(200, "OK").build();
    }

    @GET
    @Path("version")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkVersion(@QueryParam("clientHash") @DefaultValue("10") String clientHash) {
        if (!isAuthorized(clientHash, INTERFACE_VERSION, "checkVersion")) {
            return Response.status(400, "not authorized").build();
        }
        String sp = File.separator;

        String[] responseArray = getConfigValues(new String[] {"critical","newestAppVersion","urlToNewestAPK"},getBaseFolderPath()+sp+"simRa_backend.config" );
        if (responseArray != null && responseArray.length > 2) {
            return Response.status(200,responseArray[0] + "splitter" + responseArray[1] + "splitter" + responseArray[2]).build();
        } else {
            return Response.status(404, "ERROR: config could not be read").build();
        }
    }

}