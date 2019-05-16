package tuberlin.mcc.simra.backend;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Arrays;

import static tuberlin.mcc.simra.backend.control.FileListController.loadFileCSV;


public class WebServer {

    private static Logger logger = LoggerFactory.getLogger(WebServer.class.getName());

    /**
     * The jetty server serving all requests.
     */
    private final Server server;

    public WebServer() {
        int port = 8082;
        logger.info("Setting up server at port " + port);

        server = new Server(port);

        java.nio.file.Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String sp = File.separator;

        // reading fileList.csv
        loadFileCSV(absolutePath+sp+"fileList.csv");


        // servlet handlers
        ServletContextHandler servletContext =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");

        // jersey

        ResourceConfig rConfig = new ResourceConfig();
        rConfig.packages("tuberlin.mcc.simra.backend");
        ServletHolder jersey = new ServletHolder(new ServletContainer(rConfig));
        servletContext.addServlet(jersey, "/*");



        try {
            String password = "PSptb123";
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(new FileInputStream(absolutePath+sp+"keystore.jks"), password.toCharArray());

            SslContextFactory cf = new SslContextFactory();
            cf.setKeyStore(keystore);
            cf.setKeyStorePassword("PSptb123");

            HttpConfiguration config = new HttpConfiguration();
            config.addCustomizer(new SecureRequestCustomizer());
            config.setSecureScheme("https");
            config.setSecurePort(8082);

            HttpConfiguration sslConfiguration = new HttpConfiguration(config);
            sslConfiguration.addCustomizer(new SecureRequestCustomizer());
            ServerConnector sslConnector = new ServerConnector(server,
                    new SslConnectionFactory(cf, HttpVersion.HTTP_1_1.toString()),
                    new HttpConnectionFactory(sslConfiguration));
            sslConnector.setPort(8082);
            sslConnector.setName("secured_simRa");
            server.setConnectors(new Connector[]{sslConnector});
            // System.out.println(Arrays.deepToString(server.getConnectors()));
        } catch (Exception e){
            logger.error(e.getMessage());
        }


        // add handlers to HandlerList
        HandlerList handlers = new HandlerList();
        handlers.addHandler(servletContext);

        // add HanderList to server
        server.setHandler(handlers);
    }

    public void startServer() {
        try {
            server.start();
            logger.info("Server started successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            server.stop();
            logger.info("Server stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new WebServer().startServer();
    }




}
