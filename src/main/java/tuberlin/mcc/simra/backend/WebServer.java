package tuberlin.mcc.simra.backend;

import static tuberlin.mcc.simra.backend.control.FileListController.loadFileCSV;
import static tuberlin.mcc.simra.backend.control.Util.getBackendPath;
import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;

import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import tuberlin.mcc.simra.backend.control.Util;
import tuberlin.mcc.simra.backend.servlets.version13.UploadServlet;

public class WebServer {

    private static Logger logger = LoggerFactory.getLogger(WebServer.class);
    public static String secret;

    public static void main(String[] args) throws Exception {
        // Activate Jetty Logging (Development)
        // Log.setLog(new StdErrLog());

        int port = 8082;
        logger.info("Setting up server at port " + port);

        Server server = new Server(port);

        logger.info("Current Path " + getBackendPath());

        // reading fileList.csv
        loadFileCSV(getBackendPath() + "fileList.csv");

        // servlet handlers
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");

        ResourceConfig rConfig = new ResourceConfig();
        rConfig.packages("tuberlin.mcc.simra.backend");
        rConfig.register(MultiPartFeature.class);
        rConfig.register(UploadServlet.class);
        ServletHolder jersey = new ServletHolder(new ServletContainer(rConfig));
        servletContext.addServlet(jersey, "/*");
        
        // Setup SSL
        String password = null;
        String[] responseArray = getConfigValues(new String[] { "keystore_password" },
                getBackendPath() + "simRa_security.config");
        if (responseArray != null && responseArray.length > 0) {
            password = responseArray[0];
        }

        if (password != null) {

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(new FileInputStream(getBackendPath() + "certificate.jks"), password.toCharArray());
            
            SslContextFactory cf = new SslContextFactory();
            cf.setKeyStore(keystore);
            cf.setKeyStorePassword(password);
            
            HttpConfiguration config = new HttpConfiguration();
            config.addCustomizer(new SecureRequestCustomizer());
            config.setSecureScheme("https");
            config.setSecurePort(port);
            
            HttpConfiguration sslConfiguration = new HttpConfiguration(config);
            sslConfiguration.addCustomizer(new SecureRequestCustomizer());
            ServerConnector sslConnector = new ServerConnector(server,
            new SslConnectionFactory(cf, HttpVersion.HTTP_1_1.toString()),
            new HttpConnectionFactory(sslConfiguration));
            sslConnector.setPort(port);
            sslConnector.setName("secured_simRa");
            server.setConnectors(new Connector[] { sslConnector });
        }
            
        // add handlers to HandlerList
        HandlerList handlers = new HandlerList();
        handlers.addHandler(servletContext);

        // add HanderList to server
        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*new Thread(new Util.ClassificationServerStarter()).start();
        System.out.println("Classification Server started");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                ProcessBuilder classificationServerStopper = new ProcessBuilder("bash", "-c", "kill -9 $(pgrep -f \"classification_server.py\")");
                classificationServerStopper.redirectErrorStream(true);
                Process process;
                try {
                    process = classificationServerStopper.start();
                    process.waitFor();
                    System.out.println("Classification Server stopped");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
        }});*/

        System.out.println("Server started successfully");
    }

}
