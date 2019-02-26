package tuberlin.mcc.simra.backend.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("resource")
public class ResourceServlet {
    private static Logger logger = LoggerFactory.getLogger(ResourceServlet.class.getName());


    @POST
    @Path("{key}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResource(@PathParam("key") String key, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String value) {

    // logger.info("key: " + key + " value: " + value + " clientHash: " + clientHash);

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
        logger.info("key: " + key + " clientHash: " + clientHash + " serverHash: " + serverHash + " serverHash2: " + serverHash2);
        if ((!serverHash.equals(clientHash))&&(!serverHash2.equals(clientHash))){
            return Response.status(400, "not authorized").build();
        }

        return overWrite(key, value);

        /*
        if(directoryAlreadyExists(key.split("_")[0])){
        } else {

        }
        */



        // return Response.status(200, "data received and stored successfully").build();
    }

    private Response overWrite(String key, String value) {
        String sp = File.separator;


        File f = new File (sp + key.split("_")[0] + sp + key);
        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        logger.debug("key: " + key);
        logger.debug("sp + key.split(\"_\")[0] + sp + key: " + sp + key.split("_")[0] + sp + key);
        String directoryName = key.split("_")[0];
        logger.debug("directoryName: " + directoryName);
        if(!directoryAlreadyExists(directoryName)){
            try {
                Files.createDirectories(Paths.get(absolutePath+sp+key.split("_")[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter fos = new BufferedWriter(new FileWriter(absolutePath+sp+directoryName + sp + key));
        ) {
            fos.write(value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
            return Response.status(404, "Unable to store file").build();

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return Response.status(400, e.getMessage()).build();
        }
        return Response.status(200, "data received and stored successfully").build();

    }

    public static Boolean directoryAlreadyExists(String path){

        // Boolean alreadyExists = false;

        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        File file = new File(absolutePath);
        String[] fileNames = file.list();
        for (int i = 0; i < fileNames.length; i++) {
            // logger.debug("fileNames[i]: " + fileNames[i]);
            if(path.equals(fileNames[i])){
                return true;
            }
        }
        return false;
    }
}
