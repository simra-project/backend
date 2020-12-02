package tuberlin.mcc.simra.backend.control;

import org.asanchezf.SimRaNN_Test.Classifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.servlets.version10.UploadServlet;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;

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

    public static String getContentOfTextFile(String pathToTextFile) {
        File regionsFile = new File(pathToTextFile);
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

    public static ArrayList<Long> getResultsToSend(String pathToBucketsFile, Classifier c) {
        ArrayList<Long> results = new ArrayList<>();
        File bucketsFile = new File(pathToBucketsFile);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(bucketsFile)))) {
            String line;
            while ((line = br.readLine()) != null && results.size() <= 5) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
    }
