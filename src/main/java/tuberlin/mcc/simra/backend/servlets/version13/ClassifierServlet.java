package tuberlin.mcc.simra.backend.servlets.version13;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jetty.util.ajax.JSON;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static tuberlin.mcc.simra.backend.control.SimRauthenticator.isAuthorized;
import static tuberlin.mcc.simra.backend.control.Util.directoryIsFaulty;
import static tuberlin.mcc.simra.backend.control.Util.overWriteContentToFile;

@SuppressWarnings("Duplicates")
@Path("13")
public class ClassifierServlet {

    private static String sp = File.separator;
    private static int INTERFACE_VERSION = 13;
    private static long NN_VERSION = 2L;

    // private static double INCIDENT_THRESHOLD =  0.071246D;
    private static double INCIDENT_THRESHOLD =  0.06D;

    @POST
    @Path("classify-ride-cyclesense")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "Provides a Neural Network generated Incident List.", content = @Content(schema = @Schema(implementation = Array.class)))
    public Response classifyAndroidRide(@QueryParam("loc") @DefaultValue("de") String loc,
                                        @QueryParam("clientHash") @DefaultValue("10") String clientHash,
                                        @QueryParam("os") @DefaultValue("android") String os, String content) {

        if (!isAuthorized(clientHash, INTERFACE_VERSION, loc)) {
            return Response.status(400, "not authorized").build();
        }

        String randomFileName = RandomStringUtils.randomAlphanumeric(10);

        String directory = "./Backend/classify";
        if(directoryIsFaulty(directory)) {
            return Response.status(500, "directory error").build();
        }

        String simRaRidePath = directory + sp + randomFileName;
        overWriteContentToFile(simRaRidePath, content);

        ArrayList<Integer> incidentBucketNumbers;
        try {
            incidentBucketNumbers = getIncidentBucketNumbers(INCIDENT_THRESHOLD, new File(simRaRidePath).getAbsolutePath(), "http://localhost:8085", os);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(500,e.getMessage()).build();
        }
        ArrayList<Long> incidentTimestamps = getIncidentTimestamps(incidentBucketNumbers, content);

        if (!incidentTimestamps.isEmpty()) {
            ArrayList<Long> results = new ArrayList<>();
            results.add(NN_VERSION);
            results.addAll(incidentTimestamps);
            new File(simRaRidePath).delete();
            return Response.ok(JSON.toString(results)).build();
        } else {
            new File(simRaRidePath).delete();
            return Response.ok().build();
        }
    }

    private ArrayList<Integer> getIncidentBucketNumbers(double incidentThreshold, String simRaRidePath, String urlString, String os) throws IOException {
        List<String> returnLines = new ArrayList<>();
        ArrayList<Integer> incidentBucketNumbers = new ArrayList<>();

        URL url = new URL(urlString + "?os=" + os + "&ride=" + simRaRidePath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            returnLines.add(inputLine);
        }
        in.close();

        con.disconnect();

        int bucketNumber = 0;
        for (String returnLine : returnLines) {
            String thisLine = returnLine.trim();
            if (thisLine.startsWith("[")) {
                double predictedValue = Double.parseDouble(thisLine.replaceAll("\\[", "").replaceAll("]", ""));
                if (predictedValue > incidentThreshold) {
                    incidentBucketNumbers.add(bucketNumber);
                }
                bucketNumber++;
            }
        }

        return incidentBucketNumbers;
    }

    private static ArrayList<Long> getIncidentTimestamps(ArrayList<Integer> incidentBucketNumbers, String content) {
        ArrayList<Long> incidentTimestamps = new ArrayList<>();

        String[] rideLines = content.split("\n");
        int rideLineIndex = 2; // first two lines are info line and csv header, begin at line 7
        long startTS = Long.parseLong(rideLines[rideLineIndex].split(",")[5]);
        for (int i = 0; i < incidentBucketNumbers.size(); i++) {
            int thisIncidentBucketNumber = incidentBucketNumbers.get(i);
            long bucketCenter = thisIncidentBucketNumber * 10000L + 5000L;
            long lastDistanceToBucketCenter = Long.MAX_VALUE;
            long lastTimeStamp = Long.MAX_VALUE;
            for (int j = rideLineIndex; j < rideLines.length; j++) {
                String thisLine = rideLines[j];
                if (thisLine.startsWith(",,")) {
                    continue;
                }
                long thisTimeStamp = Long.parseLong(thisLine.split(",")[5]);
                long thisDistanceToBucketCenter = Math.abs(thisTimeStamp - (startTS + bucketCenter));
                if (thisDistanceToBucketCenter >= lastDistanceToBucketCenter) {
                    incidentTimestamps.add(lastTimeStamp);
                    rideLineIndex = j+1;
                    break;
                } else {
                    lastTimeStamp = thisTimeStamp;
                    lastDistanceToBucketCenter = thisDistanceToBucketCenter;
                }
                if (j == rideLines.length-1) {
                    rideLineIndex = j+1;
                    break;
                }
            }
        }
        return incidentTimestamps;
    }

}