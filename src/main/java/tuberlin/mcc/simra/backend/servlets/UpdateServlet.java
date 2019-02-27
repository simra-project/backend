package tuberlin.mcc.simra.backend.servlets;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuberlin.mcc.simra.backend.control.FileListController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static tuberlin.mcc.simra.backend.control.FileListController.updateKeyValue;

@Path("updatel")
public class UpdateServlet {
    private static Logger logger = LoggerFactory.getLogger(UpdateServlet.class.getName());
    HashMap<Integer, String> fileMap = new HashMap<>(500000);

    @POST
    @Path("{key}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResource(@PathParam("key") String key, @QueryParam("loc") @DefaultValue("de") String loc, @QueryParam("clientHash") @DefaultValue("10") String clientHash, String value) {

    // logger.info("key: " + key + " value: " + value + " clientHash: " + clientHash);

        return writeAndReturnNamePassword(key, loc, value);

    }

    private Response writeAndReturnNamePassword(String key, String loc, String value) {
        return null;
    }

}