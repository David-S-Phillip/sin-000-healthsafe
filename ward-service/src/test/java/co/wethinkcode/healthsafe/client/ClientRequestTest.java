package co.wethinkcode.healthsafe.client;
import co.wethinkcode.healthsafe.WardRecord;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientRequestTest {

    private static Javalin fakeServer;
    private static final int TEST_PORT = 7099;

    @BeforeAll
    public static void setupFakeServer(){

        //1 starting the fake server on port 7099
        fakeServer = Javalin.create().start(TEST_PORT);

        // 2. Tell it exactly what to return when the client calls it

        fakeServer.get("/wards", ctx -> {
            String fakeJson = "[{" +
                    "\"wardId\":\"TEST-1\"," +
                    "\"wing\":\"East\"," +
                    "\"department\":\"Cardiology\"," +
                    "\"bedsAvailable\":10," +
                    "\"notes\":\"Test notes\"" +
                    "}]";
            ctx.result(fakeJson);

        });
    }

    @AfterAll
    public static void stopFakeServer(){
        fakeServer.stop();
    }

    @Test
    @DisplayName("Test if we send a GET request, it succesfully responds with a json string mocking the csv data")
    public void testFetchWardsSuccessfullyParsesJson() {
        // Arrange: Point your client to the FAKE server port, not 7030
        ClientRequest client = new ClientRequest("http://localhost:" + TEST_PORT + "/wards");

        // Act: Fetch the data
        List<WardRecord> wards = client.fetchWards();

        // Assert: Prove the client correctly handled the HTTP call and Jackson deserialization
        assertNotNull(wards);
        assertEquals(1, wards.size());

        WardRecord firstWard = wards.get(0);
        assertEquals("TEST-1", firstWard.getWardId());
        assertEquals("East", firstWard.getWing());
        assertEquals(10, firstWard.getBedsAvailable());
    }

    @Test
    @DisplayName("Should return an empty list when the server is offline")
    public void testFetchWardsConnectionFailure() {
        // Point to a completely random port that we know is dead
        ClientRequest deadClient = new ClientRequest("http://localhost:9999/wards");

        List<WardRecord> wards = deadClient.fetchWards();

        // Assert the try-catch block worked and gave us an empty list, not a crash
        assertNotNull(wards);
        assertEquals(0, wards.size());
    }

}
