package co.wethinkcode.healthsafe.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AlertLevelServiceServerTest {

    private AlertLevelServiceServer server;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        // Spin up the app on port 0 (chooses a random available port to avoid collisions)
        server = new AlertLevelServiceServer();
        server.start(7099);

        // Find out what port it actually picked
        // (Assuming you expose a helper or port getter, or hardcode a test port like 7099.
        // If your server class doesn't expose port tracking yet, use a dedicated test port or check the note below).
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    // ==========================================
    // POSITIVE TESTS (2)
    // ==========================================

    @Test
    public void testGetInitialAlertLevelReturnsZero() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"level\":0}", response.body());
    }

    @Test
    public void testUpdateAlertLevelSuccessfullyChangesState() throws Exception {
        // Update level to 4 via POST
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level/4"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode());

        // Verify it changed via GET
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        assertEquals("{\"level\":4}", getResponse.body());
    }

    // ==========================================
    // NEGATIVE TESTS (2)
    // ==========================================

    @Test
    public void testUpdateAlertLevelOutOfBoundsFails() throws Exception {
        // Try to set level to 9 (max is 8)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level/9"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Error"));
    }

    @Test
    public void testUpdateAlertLevelWithInvalidFormattingFails() throws Exception {
        // Pass a string instead of an integer
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level/emergency"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Error"));
    }

    // ==========================================
    // EDGE CASE TEST (1)
    // ==========================================

    @Test
    public void testUpdateAlertLevelToAbsoluteBoundaryMax() throws Exception {
        // Test the exact upper boundary rule (8 is max Code Blue)
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level/8"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7099/alert-level"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals("{\"level\":8}", getResponse.body());
    }
}
