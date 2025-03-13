package apiinteraction;

import okhttp3.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is responsible for making API calls to the GraphHopper API.
 * It contains a method to get the walking route between two coordinates.
 */
public class ApiGraphHopperFoot {
    // The API key for the GraphHopper API
    private static final String KEY = "bf3e0c54-ef50-4c66-9e2c-ab0e70ad4868";

    /**
     * Makes a GET request to the GraphHopper API to get the walking route between
     * two coordinates.
     *
     * @param coords1String The first set of coordinates.
     * @param coords2String The second set of coordinates.
     * @return An ArrayList containing the response from the API.
     * @throws IOException If an input or output exception occurred.
     */
    public static ArrayList<String> call(String[] coords1String, String[] coords2String) throws IOException {
        // Create the request
        Request request = new Request.Builder()
                .url(STR."https://graphhopper.com/api/1/route?point=\{coords1String[0]},\{coords1String[1]}&point=\{coords2String[0]},\{coords2String[1]}&profile=foot&locale=en&calc_points=true&points_encoded=false&key=\{KEY}")
                .get()
                .build();

        // Return the response from the API
        return new ArrayList<>(Arrays.asList(GraphHopperResponse.getResponse(request)));

    }
}
