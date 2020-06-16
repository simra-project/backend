package tuberlin.mcc.simra.backend.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.servlets.version10.UploadServlet;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;

public class Util {

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());

    public static String getBaseFolderPath() {
        return System.getProperty("user.dir");
    }


    public static Boolean directoryAlreadyExists(String path){

        return Files.exists(Paths.get(path));
    }

    public static void appendTextToFile (String filepath, String content) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filepath, true)));
            out.println(content);
            out.close();
        } catch (IOException e) {
            out.println(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }

    public static boolean overWriteContentToFile(String filepath, String content){
        try (BufferedWriter fos = new BufferedWriter(new FileWriter(filepath, false))) {
            fos.write(content);
            return true;
        } catch (IOException e) {
            out.println(e.getMessage());
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
