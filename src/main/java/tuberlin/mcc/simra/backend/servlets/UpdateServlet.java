package tuberlin.mcc.simra.backend.servlets;

import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.HashMap;

@Path("upload")
public class UpdateServlet {
    private static Logger logger = LoggerFactory.getLogger(UpdateServlet.class.getName());
    Date dateToday = new Date();
    HashMap<Integer, String> fileMap = new HashMap<>(500000);

    @POST
    @Path("{key}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResource(@PathParam("key") String key, @QueryParam("loc") @DefaultValue("de") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String value) {

    // logger.info("key: " + key + " value: " + value + " clientHash: " + clientHash);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        dateToday = new Date();

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

        return writeAndReturnNamePassword(key, loc, value);

        /*
        if(directoryAlreadyExists(key.split("_")[0])){
        } else {

        }
        */



        // return Response.status(200, "data received and stored successfully").build();
    }

    // key: filename, value: content of the file
    private Response writeAndReturnNamePassword(String key, String loc, String value){
        String sp = File.separator;

        Integer hash = value.hashCode();
        String password = RandomStringUtils.randomAlphanumeric(10);

        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();

        if(!fileMap.containsKey(hash)){

            updateHashMap(hash, password, absolutePath + sp + "fileList.csv");

            System.out.println("value: " + value + " hash: " + hash + " password: " + password);

            /*
            String encrypt = Hex.encodeHexString(fileName.getBytes());

            byte[] byteArray = new byte[0];
            try {
                byteArray = Hex.decodeHex(encrypt);
            } catch (DecoderException e) {
                e.printStackTrace();
            }

            String decrypt = new String(byteArray);

            System.out.println("fileName: " + fileName + " encrypt: " + encrypt + " decrypt: " + decrypt);
            */

            logger.debug("key: " + key);
            logger.debug("sp + key.split(\"_\")[0] + sp + key: " + sp + key.split("_")[0] + sp + key);
            String directoryName = key.split("_")[0];
            logger.debug("directoryName: " + directoryName);
            if(!directoryAlreadyExists(loc)){
                try {
                    Files.createDirectories(Paths.get(loc));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // overWriteContentToFile(absolutePath + sp + "fileList.csv", fileMap.toString());
            overWriteContentToFile(absolutePath+sp+loc + sp + hash + ".csv", value);

        return Response.status(200, "data received and stored successfully").build();
        }
        return Response.status(404, "Unable to store file").build();
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

    public static Response overWriteContentToFile(String filepath, String content){
        try (BufferedWriter fos = new BufferedWriter(new FileWriter(filepath))) {
            fos.write(content);
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
    private void updateHashMap(Integer hash, String password, String filepath) {
        fileMap.put(hash, password);

        try(BufferedWriter fos = new BufferedWriter(new FileWriter(filepath))) {
            fileMap.forEach((key, value) -> {
                try { fos.write(key + "," + value + System.lineSeparator()); }
                catch (IOException ex) { throw new UncheckedIOException(ex); }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UpdateServlet updateServlet = new UpdateServlet();
        updateServlet.dateToday = new Date();
        updateServlet.writeAndReturnNamePassword("bla", "de", "Ahmet-SerdarKarakaya,JohannisthalerChaussee422,12351Berlin");
    }

}