package tuberlin.mcc.simra.backend.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.servlets.UploadServlet;

import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileListController {
    private static HashMap<Integer, String> fileMap = new HashMap<>(500000);

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());

    public static void updateKeyValue (Integer key, String value, String filePath) {
        fileMap.put(key, value);
        try(BufferedWriter fos = new BufferedWriter(new FileWriter(filePath))) {
            fileMap.forEach((k, v) -> {
                try { fos.write(k + "," + v + System.lineSeparator()); }
                catch (IOException ex) { throw new UncheckedIOException(ex); }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean containsKey (Integer key) {
        return fileMap.containsKey(key);
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
