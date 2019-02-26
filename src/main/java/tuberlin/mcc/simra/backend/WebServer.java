package tuberlin.mcc.simra.backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.servlets.StatusServlet;

import javax.servlet.ServletException;

public class WebServer {

    private static Logger logger = LoggerFactory.getLogger(WebServer.class.getName());

    /**
     * The jetty server serving all requests.
     */
    private final Server server;

    public WebServer() {
        int port = 8080;
        logger.info("Setting up server at port " + port);

        server = new Server(port);

        // servlet handlers
        ServletContextHandler servletContext =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");

        // jersey

        ResourceConfig config = new ResourceConfig();
        config.packages("tuberlin.mcc.simra.backend");
        ServletHolder jersey = new ServletHolder(new ServletContainer(config));
        servletContext.addServlet(jersey, "/*");



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




}
