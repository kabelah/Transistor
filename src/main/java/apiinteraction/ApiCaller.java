package apiinteraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller {
    public static String error;
    public static int errorCode;
    private static final boolean IS_TEST_ENVIRONMENT = true; // Flag to switch between environments

    public static String[] getCoordinates(String postcode) throws IOException {
        error = "";
        String endpoint = IS_TEST_ENVIRONMENT
                ? "http://localhost:8080/get_coordinates?postcode=" + postcode // Use HTTP for testing
                : "https://computerscience.dacs.unimaas.nl/get_coordinates?postcode=" + postcode; // Use HTTPS for production

        BufferedReader reader = getBufferedReader(endpoint, postcode);

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();

        if (errorCode == HttpURLConnection.HTTP_OK) {
            int latStart = stringBuilder.indexOf("\"latitude\":") + "\"latitude\":".length() + 2;
            int lonStart = stringBuilder.indexOf("\"longitude\":") + "\"longitude\":".length() + 2;

            // Extract latitude and longitude strings
            String latExtract = stringBuilder.substring(latStart, stringBuilder.indexOf("\"", latStart));
            String lonExtract = stringBuilder.substring(lonStart, stringBuilder.indexOf("\"", lonStart));

            return new String[]{latExtract, lonExtract};
        } else {
            // Handle error response
            throw new IOException("Server returned non-OK status: " + errorCode);
        }
    }

    private static BufferedReader getBufferedReader(String endpoint, String postcode) throws IOException {
        HttpURLConnection conn = getHttpURLConnection(endpoint, postcode);

        // Check response code to decide which stream to read
        errorCode = conn.getResponseCode();

        if (errorCode == HttpURLConnection.HTTP_OK) {
            return new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            // If not OK, read the error stream
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            error = errorResponse.toString();
            errorReader.close(); // Close errorReader

            // Log or throw an exception with the error response
            throw new IOException("Server returned non-OK status: " + errorCode);
        }
    }

    private static HttpURLConnection getHttpURLConnection(String endpoint, String postcode) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Use try-with-resources to ensure resources are closed properly
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            String payload = "{\"postcode\" : \"" + postcode + "\"}";
            writer.write(payload);
            writer.flush();
        } // OutputStreamWriter is automatically closed here
        return conn;
    }
}
