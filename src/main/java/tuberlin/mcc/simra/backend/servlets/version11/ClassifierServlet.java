package tuberlin.mcc.simra.backend.servlets;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import preprocessing.AdaptRide;
import org.asanchezf.SimRaNN_Test.Classifier;
import static tuberlin.mcc.simra.backend.control.Util.*;
import java.nio.file.Files;
import org.eclipse.jetty.util.ajax.JSON;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.RandomStringUtils;

@SuppressWarnings("Duplicates")
@Path("11")
public class ClassifierServlet {

    String analyzeFileName = "ride.csv";

    @POST
    @Path("classify-ride")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "Provides a Neural Network generated Incident List.", content = @Content(schema = @Schema(implementation = Array.class)))
    public Response classifyRide(@QueryParam("bikeType") String bikeType,
    @QueryParam("phoneLocation") String phoneLocation, String content) {

        if(bikeType == null || phoneLocation == null){
            throw new WebApplicationException(
            Response.status(Response.Status.BAD_REQUEST)
                .entity("parameters are mandatory")
                .build()
            );
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

        String preContent = "0#0\nkey,lat,lon,ts,bike,childCheckBox,trailerCheckBox,pLoc,incident,i1,i2,i3,i4,i5,i6,i7,i8,i9,scary,desc,i10\n0,0,0,0," + bikeType + ",0,0," + phoneLocation + ",,,,,,,,,,,,,0\n\n=========================\n";

        overWriteContentToFile(directory + File.separator + randomString, preContent + content);

        String inPath = directory + File.separator + randomString;
        String outPath = directory + File.separator + randomString + ".csv";
        AdaptRide adaptRide = new AdaptRide(inPath, outPath);
        adaptRide.run_preprocessing();

        Classifier c = null;
        try {
            String rp = outPath;
            String mp = "./DSNet1_v2.zip";
            c = new Classifier(rp, mp, 0.50);
            c.run_classifier();
            System.out.println(c.getResults());
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