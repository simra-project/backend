package tuberlin.mcc.simra.backend.servlets.version7;

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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static tuberlin.mcc.simra.backend.control.FileListController.checkKeyValue;
import static tuberlin.mcc.simra.backend.control.FileListController.overWriteContentToFile;

@SuppressWarnings("Duplicates")
@Path("7")
public class UploadServlet {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());

    @POST
    @Path("upload")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadPost(@QueryParam("fileName") String fileName, @QueryParam("loc") @DefaultValue("de") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {


     // logger.info("fileName: " + fileName + " version: " + version + " loc: " + loc + " clientHash: " + clientHash + " content: " + content);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date dateToday = new Date();

        String oauth = sdf.format(dateToday);

        oauth += "mcc_simra";

        Date dateTomorrow = new Date(dateToday.getTime()+(1000*24*60*60));
        String oauth2 = sdf.format(dateTomorrow);
        oauth2 += "mcc_simra";

        int hash = oauth.hashCode();
        String serverHash = Integer.toHexString(hash);

        int hash2 = oauth2.hashCode();
        String serverHash2 = Integer.toHexString(hash2);

        logger.info("dateToday: " + dateToday);
        logger.info("beforeHash: " + oauth);
        logger.info("fileName: " + fileName + " version: 7" + " loc: " + loc + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash))&&(!serverHash2.equals(clientHash))){
            return Response.status(400, "not authorized").build();
        }

        return writeAndReturnNamePassword(fileName, "7", loc, content);

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

        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        if(!FileListController.containsKey(hash)){

            String directory;
            if (fileName.equals("profile.csv")) {
                directory = version + sp + loc + sp + "Profiles";
                FileListController.updateKeyValue(hash, password, absolutePath + sp + "fileList.csv");
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
                FileListController.updateKeyValue(hash, password, absolutePath + sp + "fileList.csv");
            }
            if(!FileListController.directoryAlreadyExists(directory)){
                try {
                    Files.createDirectories(Paths.get(directory));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileName.startsWith("CRASH")) {
                String[] fileNameLineArray = fileName.split("_");
                fileName = Arrays.toString(Arrays.copyOfRange(fileNameLineArray,2,(fileNameLineArray.length))).replace("[","").replace(",","").replace("]","");
                FileListController.overWriteContentToFile(absolutePath + sp + directory + sp + fileName + ".csv", content);
            } else {
                FileListController.overWriteContentToFile(absolutePath + sp + directory + sp + hash + ".csv", content);
            }

            return Response.status(200, hash + "," + password).build();
        }
        return Response.status(404, "Unable to store file").build();
    }

    @PUT
    @Path("update")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePut(@QueryParam("fileHash") String fileHash, @QueryParam("filePassword") String filePassword, @QueryParam("loc") @DefaultValue("Berlin") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {


        // logger.info("fileHash: " + fileHash + " version: " + version + " loc: " + loc + " clientHash: " + clientHash + " content: " + content);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date dateToday = new Date();

        String oauth = sdf.format(dateToday);

        oauth += "mcc_simra";

        Date dateTomorrow = new Date(dateToday.getTime()+(1000*24*60*60));
        String oauth2 = sdf.format(dateTomorrow);
        oauth2 += "mcc_simra";

        int hash = oauth.hashCode();
        String serverHash = Integer.toHexString(hash);

        int hash2 = oauth2.hashCode();
        String serverHash2 = Integer.toHexString(hash2);

        logger.info("dateToday: " + dateToday);
        logger.info("beforeHash: " + oauth);
        logger.info("fileHash: " + fileHash + " filePassword: " + filePassword + " version: 7" + " loc: " + loc + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: " + serverHash2 + " content: " + content);
        if (((!serverHash.equals(clientHash))&&(!serverHash2.equals(clientHash)))||(!checkKeyValue(fileHash,filePassword))){
            return Response.status(400, "not authorized").build();
        }


        return overWriteAndReturnStatus(fileHash, "7", loc, content);

    }

    // fileHash: filename, content: content of the file
    private Response overWriteAndReturnStatus(String fileHash, String version, String loc, String content){
        String sp = File.separator;

        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        String directory;

        if (fileHash.contains("profile.csv")) {
            directory = version + sp + loc + sp + "Profiles";
            fileHash = fileHash.replace("profile.csv", "");
        } else {
            directory = version + sp + loc + sp + "Rides";
        }
        if(!FileListController.directoryAlreadyExists(directory)){
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("writing to filePath: " + absolutePath + sp + directory + sp + fileHash + ".csv");
        logger.info("writing content: " + content);
        FileListController.overWriteContentToFile(absolutePath + sp + directory + sp + fileHash + ".csv", content);

        return Response.status(200, "OK").build();
    }



    /*
    public static void main(String[] args) {
        UploadServlet resourceServlet = new UploadServlet();
        resourceServlet.dateToday = new Date();
        resourceServlet.writeAndReturnNamePassword("bla", "de", "Ahmet-SerdarKarakaya,JohannisthalerChaussee422,12351Berlin");
    }
    */

}