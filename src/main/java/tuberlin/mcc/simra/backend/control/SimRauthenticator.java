package tuberlin.mcc.simra.backend.control;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;
import static tuberlin.mcc.simra.backend.control.Util.getBaseFolderPath;

public class SimRauthenticator {

    public static String[] getHashes() {
        String prefix = null;
        String sp = File.separator;

        String[] responseArray = getConfigValues(new String[] {"hash_prefix"},getBaseFolderPath()+sp+"simRa_security.config" );
        if (responseArray != null && responseArray.length > 0) {
            prefix = responseArray[0];
        }

        String serverHash = prefix;

        String serverHash2 = prefix;

        return new String[] {serverHash,serverHash2};
    }

}
