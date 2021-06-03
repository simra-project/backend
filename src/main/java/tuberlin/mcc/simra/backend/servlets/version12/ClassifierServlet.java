package tuberlin.mcc.simra.backend.servlets.version12;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.asanchezf.SimRaNN_Test.Classifier;
import org.eclipse.jetty.util.ajax.JSON;
import preprocessing.AdaptRide;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.isAuthorized;
import static tuberlin.mcc.simra.backend.control.Util.directoryIsFaulty;
import static tuberlin.mcc.simra.backend.control.Util.overWriteContentToFile;

@SuppressWarnings("Duplicates")
@Path("12")
public class ClassifierServlet {

    private static String sp = File.separator;
    private static int INTERFACE_VERSION = 12;
    private static long NN_VERSION = 1L;


    @POST
    @Path("classify-ride")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "Provides a Neural Network generated Incident List.", content = @Content(schema = @Schema(implementation = Array.class)))
    public Response classifyRide(@QueryParam("bikeType") String bikeType,
            @QueryParam("phoneLocation") String phoneLocation, @QueryParam("loc") @DefaultValue("de") String loc,
                                 @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash, INTERFACE_VERSION, loc)) {
            return Response.status(400, "not authorized").build();
        }
        if (bikeType == null || phoneLocation == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity("parameters are mandatory").build());
        }

        String randomFileName = RandomStringUtils.randomAlphanumeric(10);

        String directory = "./classify";
        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }
        String preContent = "0#0\nkey,lat,lon,ts,bike,childCheckBox,trailerCheckBox,pLoc,incident,i1,i2,i3,i4,i5,i6,i7,i8,i9,scary,desc,i10\n0,0,0,0,"
                + bikeType + ",0,0," + phoneLocation + ",,,,,,,,,,,,,0\n\n=========================\n";

        String simRaRidePath = directory + sp + randomFileName;
        overWriteContentToFile(simRaRidePath, preContent + content);

        String adaptedRidePath = simRaRidePath + ".csv";
        AdaptRide adaptRide = new AdaptRide(simRaRidePath);
        Classifier c = null;
        try {
            adaptRide.run_preprocessing();

            String pathToNN = "./DSNet1_v2.zip";
            c = new Classifier(adaptedRidePath, pathToNN);
            c.run_classifier();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (c != null) {
            ArrayList<Long> results = new ArrayList<>();
            results.add(NN_VERSION);
            results.addAll(c.getResults(adaptedRidePath));
            new File(adaptedRidePath).delete();
            new File(simRaRidePath).delete();
            new File(simRaRidePath.replace(".csv","_timestamps.csv")).delete();
            return Response.ok(JSON.toString(results)).build();
        } else {
            new File(adaptedRidePath).delete();
            new File(simRaRidePath).delete();
            new File(simRaRidePath.replace(".csv","_timestamps.csv")).delete();
            return Response.ok().build();
        }
    }
}