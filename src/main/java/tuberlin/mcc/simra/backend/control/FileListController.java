package tuberlin.mcc.simra.backend.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.servlets.version10.UploadServlet;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

import static tuberlin.mcc.simra.backend.control.Util.appendTextToFile;
import static tuberlin.mcc.simra.backend.control.Util.overWriteContentToFile;

public class FileListController {
    private static ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>(500000);

    private static Logger logger = LoggerFactory.getLogger(UploadServlet.class.getName());

    public static void updateKeyValue (String key, String value, String filePath) {
        fileMap.put(key, value);
        appendTextToFile(filePath, key + "," + value);
        /*
        try(BufferedWriter fos = new BufferedWriter(new FileWriter(filePath))) {
            fileMap.forEach((k, v) -> {
                try { fos.write(k + "," + v + System.lineSeparator()); }
                catch (IOException ex) { throw new UncheckedIOException(ex); }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public static Boolean checkKeyValue(String key, String value) {
        if (!containsKey(key)) {
            return false;
        }
        String actualValue = fileMap.get(key);
        if (actualValue.equals(value)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean containsKey (String key) {
        return fileMap.containsKey(key);
    }

    public static void loadFileCSV(String pathToFile) {
        logger.info("loading fileList.csv under path: " + pathToFile);
        File fileCSV = new File(pathToFile);


        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileCSV)))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] actualLine = line.split(",");
                if (actualLine.length == 2) {
                    fileMap.put(actualLine[0],actualLine[1]);
                }
            }
        } catch (FileNotFoundException fnfe) {
            overWriteContentToFile(pathToFile,"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
