package apiinteraction;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import okhttp3.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphHopperResponseTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8080); // Start WireMock server on port 8080
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
        GraphHopperResponse.distance = null;
        GraphHopperResponse.responseCode = 0;
        GraphHopperResponse.errorCode = "";
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop(); // Stop WireMock server after tests
    }

    @Test
    void testGetResponse_SuccessfulResponse() throws IOException {
        String mockJson = "{\"paths\":[{\"distance\":1234.5,\"instructions\":[],\"coordinates\":[[10.0,20.0],[30.0,40.0]]}]}";

        stubFor(get(urlEqualTo("/route"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockJson)));

        Request request = new Request.Builder().url("http://localhost:8080/route").build();
        String[] coordinates = GraphHopperResponse.getResponse(request);

        assertEquals("1234.5", GraphHopperResponse.distance);
        assertEquals(200, GraphHopperResponse.responseCode);
        assertArrayEquals(new String[]{"10.0,20.0", "30.0,40.0"}, coordinates);
        assertEquals("", GraphHopperResponse.errorCode);
    }

    @Test
    void testGetResponse_ErrorResponse() throws IOException {
        String expectedErrorResponse = "{\"error\": \"Invalid request\"}";

        stubFor(get(urlEqualTo("/route"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody(expectedErrorResponse)));

        Request request = new Request.Builder().url("http://localhost:8080/route").build();

        String[] coordinates = GraphHopperResponse.getResponse(request);

        assertEquals("ParsingError", GraphHopperResponse.errorCode);
        assertEquals(400, GraphHopperResponse.responseCode);
        assertArrayEquals(new String[0], coordinates);
    }

    @Test
    void testGetResponse_NoInternet() throws IOException {
        stubFor(get(urlEqualTo("/route"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        Request request = new Request.Builder().url("http://localhost:8080/route").build();

        String[] coordinates = GraphHopperResponse.getResponse(request);

        assertEquals("NoInternet", GraphHopperResponse.errorCode);
        assertArrayEquals(new String[0], coordinates);
    }
}
