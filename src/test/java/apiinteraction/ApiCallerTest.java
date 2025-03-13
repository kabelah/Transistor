package apiinteraction;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class ApiCallerTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8080); // Start WireMock server on port 8080
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
        ApiCaller.errorCode = 0;
        ApiCaller.error = "";
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop(); // Stop WireMock server after tests
    }

    @Test
    void testGetCoordinates_SuccessfulResponse() throws IOException {
        String postcode = "1234AB";
        String expectedResponse = "{\"latitude\": \"51.1234\", \"longitude\": \"5.6789\"}";

        stubFor(post(urlEqualTo("/get_coordinates?postcode=1234AB"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(expectedResponse)));

        String[] coordinates = ApiCaller.getCoordinates(postcode);

        assertEquals("51.1234", coordinates[0]);
        assertEquals("5.6789", coordinates[1]);
        assertEquals("", ApiCaller.error);
        assertEquals(200, ApiCaller.errorCode); // Changed from 0 to 200
    }

    @Test
    void testGetCoordinates_ErrorResponse() throws IOException {
        String postcode = "9999ZZ";
        String expectedErrorResponse = "{\"error\": \"Invalid postcode\"}";

        stubFor(post(urlEqualTo("/get_coordinates?postcode=9999ZZ"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody(expectedErrorResponse)));

        IOException exception = assertThrows(IOException.class, () -> ApiCaller.getCoordinates(postcode));
        assertEquals("Server returned non-OK status: 400", exception.getMessage());
        assertEquals(expectedErrorResponse, ApiCaller.error);
        assertEquals(400, ApiCaller.errorCode);
    }

    @Test
    void testGetCoordinates_IOExceptionThrown() {
        String postcode = "5678CD";

        stubFor(post(urlEqualTo("/get_coordinates?postcode=5678CD"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThrows(IOException.class, () -> ApiCaller.getCoordinates(postcode));
    }
}
