package co.wethinkcode.healthsafe.server;
import co.wethinkcode.healthsafe.WardRecord;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WardApiServerTest {
    private WardApiServer testServer;
    private int testPort;

    @BeforeEach
    void startFakeServer(){
        // this starts up our ward-service server and we pass a list with information from the csv file
        List<WardRecord> fakeWards = List.of(
                new WardRecord("W-01", "East Wing", "Cardiology", 3, "N/A"),
                new WardRecord("W-02", "West Wing", "Neuroglogy", 10, "The president booked the floor")
        );

        // Inject it into the server and start on a random open port
        testServer = new WardApiServer(fakeWards);
        testServer.start(0);

        // 3. Ask the server which port Javalin actually picked
        // (Make sure you add a `public int port() { return app.port(); }` method to WardApiServer)
        testPort = testServer.port();
    }

    @AfterEach
    void stopFakeServer(){
        testServer.stop();
    }

    @Test
    @DisplayName("GET /wards/{id} should return 200 and the correct JSON for a valid ID")
    void testGetValidWard() {
        // Arrange & Act
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:" + testPort + "/wards/W-01").asJson();

        // Assert HTTP Status
        assertEquals(200, response.getStatus());

        // Assert JSON Payload
        assertEquals("Cardiology", response.getBody().getObject().getString("department"));
        assertEquals(3, response.getBody().getObject().getInt("bedsAvailable"));
    }

    @Test
    @DisplayName("GET /wards/{id} should return 404 for an ID that does not exist")
    void testGetInvalidWard() {
        // Arrange & Act
        HttpResponse<String> response = Unirest.get("http://localhost:" + testPort + "/wards/GHOST-WARD").asString();

        // Assert HTTP Status and body
        assertEquals(404, response.getStatus());
        assertEquals("Ward not found", response.getBody());
    }

    @Test
    @DisplayName("GET /departments should return a list of unique departments")
    void testGetDepartments() {
        // Arrange & Act
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:" + testPort + "/departments").asJson();

        // Assert
        assertEquals(200, response.getStatus());
        // Since both W-01 and W-02 have different departments, the array length should be 2
        assertEquals(2, response.getBody().getArray().length());
    }

}
