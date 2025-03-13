package apiinteraction;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class is responsible for handling the response from the GraphHopper API.
 */
public class GraphHopperResponse {
    public static String distance;
    public static int responseCode;
    public static String errorCode;

    /**
     * Gets the response from the API and extracts the coordinates of the route.
     *
     * @param request The request to the API.
     * @return An array containing the coordinates of the route.
     * @throws IOException If an input or output exception occurred.
     */
    public static String[] getResponse(Request request) throws IOException {
        // Initialize the response code and error code
        distance = null;
        errorCode = "";
        try {
            OkHttpClient client = new OkHttpClient();
            // Execute the request and get the response
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new IOException("Response body is null");
            }
            String responseBody = response.body().string();
            responseCode = response.code();

            // Find the start of the distance in the response and extract it
            int distanceStart = responseBody.indexOf("\"distance\":");
            if (distanceStart == -1) {
                throw new IOException("Distance field not found in response");
            }
            String distanceAndRest = responseBody.substring(distanceStart + "\"distance\":".length());
            distance = distanceAndRest.substring(0, distanceAndRest.indexOf(","));

            // Find the start and end of the coordinates in the response and extract them
            int coordsStart = responseBody.indexOf("\"coordinates\":[[");
            int coordsEnd = responseBody.indexOf("]]", coordsStart);
            if (coordsStart == -1 || coordsEnd == -1) {
                throw new IOException("Coordinates field not found in response");
            }
            String coordsExtract = responseBody.substring(coordsStart + "\"coordinates\":[".length(), coordsEnd + 1);

            // Ensure the coordinates are properly formatted
            coordsExtract = coordsExtract.replace("],[", ";").replace("[", "").replace("]", "");
            return coordsExtract.split(";");
        } catch (UnknownHostException | SocketException e) {
            // Set the error code if there is no internet connection
            errorCode = "NoInternet";
        } catch (IOException e) {
            // Log the error message and set error code
            System.err.println("Error parsing the response: " + e.getMessage());
            errorCode = "ParsingError";
        }
        // Return an empty array if an exception occurred
        return new String[0];
    }
}
