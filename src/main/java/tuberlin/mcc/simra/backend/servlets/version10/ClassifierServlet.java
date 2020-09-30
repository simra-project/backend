package tuberlin.mcc.simra.backend.servlets.version10;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.isAuthorized;
import static tuberlin.mcc.simra.backend.control.Util.directoryAlreadyExists;
import static tuberlin.mcc.simra.backend.control.Util.overWriteContentToFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.asanchezf.SimRaNN_Test.Classifier;
import org.eclipse.jetty.util.ajax.JSON;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import preprocessing.AdaptRide;

@SuppressWarnings("Duplicates")
@Path("10")
public class ClassifierServlet {

    String analyzeFileName = "ride.csv";
    private static String sp = File.separator;


    @POST
    @Path("classify-ride")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "Provides a Neural Network generated Incident List.", content = @Content(schema = @Schema(implementation = Array.class)))
    public Response classifyRide(@QueryParam("bikeType") String bikeType,
            @QueryParam("phoneLocation") String phoneLocation, @QueryParam("loc") @DefaultValue("de") String loc,
                                 @QueryParam("clientHash") @DefaultValue("10") String clientHash, String content) {

        if (!isAuthorized(clientHash, -1, loc)) {
            return Response.status(400, "not authorized").build();
        }
        if (bikeType == null || phoneLocation == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity("parameters are mandatory").build());
        }

        String randomString = RandomStringUtils.randomAlphanumeric(10);

        String directory = "./classify";
        if (!directoryAlreadyExists(directory)) {
            try {
                Files.createDirectories(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String preContent = "0#0\nkey,lat,lon,ts,bike,childCheckBox,trailerCheckBox,pLoc,incident,i1,i2,i3,i4,i5,i6,i7,i8,i9,scary,desc,i10\n0,0,0,0,"
                + bikeType + ",0,0," + phoneLocation + ",,,,,,,,,,,,,0\n\n=========================\n";

        overWriteContentToFile(directory + sp + randomString, preContent + content);

        String inPath = directory + sp + randomString;
        String outPath = directory + sp + randomString + ".csv";
        AdaptRide adaptRide = new AdaptRide(inPath, outPath);
        Classifier c = null;
        try {
            System.out.println("adaptRide.run_preprocessing();");
            adaptRide.run_preprocessing();

            String rp = outPath;
            String mp = "./DSNet1_v2.zip";
            System.out.println("c = new Classifier(rp, mp, 0.50);");
            c = new Classifier(rp, mp, 0.50);
            System.out.println("c.run_classifier();");
            c.run_classifier();
            System.out.println("hi");
            System.out.println(c.getResults());
            System.out.println("hi2");
        } catch (Exception e) {
            e.printStackTrace();
        }

        new File(outPath).delete();
        new File(inPath).delete();

        if (c != null) {
            return Response.ok(JSON.toString(c.getResults())).build();
        } else {
            return Response.ok().build();
        }
    }
}