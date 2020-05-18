package tuberlin.mcc.simra.backend.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.servlets.version10.UploadServlet;

import java.io.*;
import java.nio.file.Paths;

public class Util {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());


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

    public static void appendTextToFile (String filepath, String content) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filepath, true)));
            out.println(content);
            out.close();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static boolean overWriteContentToFile(String filepath, String content){
        try (BufferedWriter fos = new BufferedWriter(new FileWriter(filepath, false))) {
            fos.write(content);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public static String[] getConfigValues (String[] keys, String pathToConfig) {

        File configFile = new File(pathToConfig);
        String[] result = new String[keys.length];


        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)))) {
            String line;

            while ((line = br.readLine()) != null) {
                for (int i = 0; i < keys.length; i++) {
                    if (line.startsWith(keys[i])) {
                        result[i] = line.split("=")[1];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getRegions (String pathToRegions) {
        File regionsFile = new File(pathToRegions);
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(regionsFile)))) {
            String line;

            while ((line = br.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
