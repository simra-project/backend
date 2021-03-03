package tuberlin.mcc.simra.backend.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SimRauthenticator {
    private static Logger logger = LoggerFactory.getLogger(SimRauthenticator.class.getName());

    public static boolean isAuthorized(String clientHash, int interfaceVersion, String loc) {
        String prefix = null;
        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String sp = File.separator;

        String[] responseArray = getConfigValues(new String[] { "hash_prefix" },
                absolutePath + sp + "simRa_security.config");
        if (responseArray != null && responseArray.length > 0) {
            prefix = responseArray[0];
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date dateToday = new Date();

        String oauth = sdf.format(dateToday);

        oauth += prefix;

        Date dateTomorrow = new Date(dateToday.getTime() + (1000 * 24 * 60 * 60));
        String oauth2 = sdf.format(dateTomorrow);
        oauth2 += prefix;

        int hash = oauth.hashCode();
        String serverHash = Integer.toHexString(hash);

        int hash2 = oauth2.hashCode();
        String serverHash2 = Integer.toHexString(hash2);

        logger.info("interfaceVersion: " + interfaceVersion + " loc: " + loc + "clientHash: " + clientHash
                + " serverHash: " + serverHash + " serverHash2: " + serverHash2);

        return ((serverHash.equals(clientHash)) || (serverHash2.equals(clientHash))
                || (("0" + serverHash).equals(clientHash)) || (("0" + serverHash2).equals(clientHash)));

            // return Response.status(400, "not authorized").build();

    }


}