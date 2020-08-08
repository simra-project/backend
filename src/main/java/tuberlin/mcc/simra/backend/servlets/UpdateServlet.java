package tuberlin.mcc.simra.backend.servlets;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ####################################################
// WARNING!
// This file should not be used anymore. 
// It should be deleted as soon as guaranteed that no old app version is using this endoint 
// Please modify it in the according api version folder!
// ####################################################

@Path("updatel")
public class UpdateServlet {
    private static Logger logger = LoggerFactory.getLogger(UpdateServlet.class.getName());
    HashMap<Integer, String> fileMap = new HashMap<>(500000);

    @POST
    @Path("{key}")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResource(@PathParam("key") String key, @QueryParam("loc") @DefaultValue("de") String loc,
            @QueryParam("clientHash") @DefaultValue("10") String clientHash, String value) {

        // logger.info("key: " + key + " value: " + value + " clientHash: " +
        // clientHash);

        return writeAndReturnNamePassword(key, loc, value);

    }

    private Response writeAndReturnNamePassword(String key, String loc, String value) {
        return null;
    }

}