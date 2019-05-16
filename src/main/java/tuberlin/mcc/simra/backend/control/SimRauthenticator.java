package tuberlin.mcc.simra.backend.control;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimRauthenticator {

    public static String[] getHashes(String prefix) {
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
