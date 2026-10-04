package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IngestionServerTest {

    private IngestionServer server;
    // We use a different port than 7030 so tests don't crash if the main app is running
    private final int TEST_PORT = 7077;

    @BeforeEach
    public void startServer() {
        server = new IngestionServer();
        server.start(TEST_PORT);
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void testHealthEndpointReturns200() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
    }

    @Test
    public void testWardsEndpointReturnsJsonData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/wards"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 1. Check that the server actually responded successfully
        assertEquals(200, response.statusCode());

        // 2. Check that Jackson successfully serialized the objects into JSON
        String responseBody = response.body();
        assertTrue(responseBody.contains("wardId"), "Response should contain JSON keys");

        // 3. Verify specific data from your wards-outdated.csv made it through the pipeline
        // (Assuming W-05 is one of the cleaned IDs from your file)
        assertTrue(responseBody.contains("W-05"), "Response should contain cleaned ward W-05");
    }


    @Test
    public void testInvalidNumberIsHandledGracefully() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/wards"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 1. Ensure the request succeeded
        assertEquals(200, response.statusCode());

        String responseBody = response.body();

        // 2. Assert that the non-numeric input ("five") didn't crash the server,
        // but instead caught the exception and added the warning note to the JSON output.
        assertTrue(responseBody.contains("bedsAvailable was non-numeric"),
                "Response should flag non-numeric bed values");

        // 3. Verify that bedsAvailable for that specific entry translates to null in JSON (which appears as null)
        assertTrue(responseBody.contains("\"bedsAvailable\":null"),
                "Beds available should be set to null when invalid");
    }
}