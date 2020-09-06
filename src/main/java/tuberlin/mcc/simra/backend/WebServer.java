package tuberlin.mcc.simra.backend;

import static tuberlin.mcc.simra.backend.control.FileListController.loadFileCSV;
import static tuberlin.mcc.simra.backend.control.Util.getBaseFolderPath;
import static tuberlin.mcc.simra.backend.control.Util.getConfigValues;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

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
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static Logger logger = LoggerFactory.getLogger(WebServer.class.getName());
    public static String secret;

    public static void main(String[] args) throws Exception {
        // Activate Jetty Logging (Development)
        Log.setLog(new StdErrLog());

        int port = 8080;
        logger.info("Setting up server at port " + port);

        Server server = new Server(port);

        logger.info("Current Path " + getBaseFolderPath());
        String sp = File.separator;

        // reading fileList.csv
        loadFileCSV(getBaseFolderPath() + sp + "fileList.csv");

        // servlet handlers
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");

        ResourceConfig rConfig = new ResourceConfig();
        rConfig.packages("tuberlin.mcc.simra.backend");
        ServletHolder jersey = new ServletHolder(new ServletContainer(rConfig));
        servletContext.addServlet(jersey, "/*");
        
        // Setup SSL
        String password = null;
        String[] responseArray = getConfigValues(new String[] { "keystore_password" },
                getBaseFolderPath() + sp + "simRa_security.config");
        if (responseArray != null && responseArray.length > 0) {
            password = responseArray[0];
        }

        if(password != null){

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(new FileInputStream(getBaseFolderPath() + sp + "certificate.jks"), password.toCharArray());
            
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
        logger.info("Server started successfully");
    }

}
