package tuberlin.mcc.simra.backend.control;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;

public class SimRauthenticator {

    public static String[] getHashes() {
        String prefix = null;
        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String sp = File.separator;

        String[] responseArray = getConfigValues(new String[] {"hash_prefix"},absolutePath+sp+"simRa_security.config" );
        if (responseArray != null && responseArray.length > 0) {
            prefix = responseArray[0];
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date dateToday = new Date();

        String oauth = sdf.format(dateToday);

        oauth += prefix;

        Date dateTomorrow = new Date(dateToday.getTime()+(1000*24*60*60));
        String oauth2 = sdf.format(dateTomorrow);
        oauth2 += prefix;

        int hash = oauth.hashCode();
        String serverHash = Integer.toHexString(hash);

        int hash2 = oauth2.hashCode();
        String serverHash2 = Integer.toHexString(hash2);

        return new String[] {serverHash,serverHash2};
    }

}
