package tuberlin.mcc.simra.backend.servlets;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.control.FileListController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("upload")
public class UploadServlet {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());

    @POST
    @Path("{key}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResource(@PathParam("key") String key, @QueryParam(("version")) String version, @QueryParam("loc") @DefaultValue("de") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        logger.info("wrong neighborhood");
        /*
     // logger.info("key: " + key + " version: " + version + " loc: " + loc + " clientHash: " + clientHash + " content: " + content);

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
        logger.info("key: " + key + " version: " + version + " loc: " + loc + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash))&&(!serverHash2.equals(clientHash))){
            return Response.status(400, "not authorized").build();
        }

        return writeAndReturnNamePassword(key, version, loc, content);
        */
        return Response.status(400, "wrong neighborhood").build();
    }

    // key: filename, content: content of the file
    private Response writeAndReturnNamePassword(String key, String version, String loc, String content){
        String sp = File.separator;
        String fileBody = content.substring(content.indexOf(System.lineSeparator())+1);
        Integer hash = fileBody.hashCode();
        if(key.equals("profile.csv")){
           hash = (RandomStringUtils.randomAlphanumeric(30)).hashCode();
        }
        String password = RandomStringUtils.randomAlphanumeric(10);

        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        if(!FileListController.containsKey(hash)){

            String directory;
            if (key.equals("profile.csv")) {
                directory = version + sp + loc + sp + "Profiles";
                FileListController.updateKeyValue(hash, password, absolutePath + sp + "fileList.csv");
            } else if (key.startsWith("CRASH")) {
                directory = version + sp + loc + sp + "CRASH";
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
            if (key.startsWith("CRASH")) {
                FileListController.overWriteContentToFile(absolutePath + sp + directory + sp + key + ".csv", content);
            } else {
                FileListController.overWriteContentToFile(absolutePath + sp + directory + sp + hash + ".csv", content);
            }

            return Response.status(200, hash + "," + password).build();
        }
        return Response.status(404, "Unable to store file").build();
    }

    /*
    public static void main(String[] args) {
        UploadServlet resourceServlet = new UploadServlet();
        resourceServlet.dateToday = new Date();
        resourceServlet.writeAndReturnNamePassword("bla", "de", "Ahmet-SerdarKarakaya,JohannisthalerChaussee422,12351Berlin");
    }
    */

}