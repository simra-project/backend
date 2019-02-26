package tuberlin.mcc.simra.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {

    public static void main(String[] args){
        WebServer webServer = new WebServer();
        webServer.startServer();

    }
}
