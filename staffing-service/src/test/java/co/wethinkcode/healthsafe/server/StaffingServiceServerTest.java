package co.wethinkcode.healthsafe.server;

import co.wethinkcode.healthsafe.client.StaffingServiceClient;
import co.wethinkcode.healthsafe.mq.StaffingPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StaffingServiceServerTest {

    private StaffingServiceClient mockClient;
    private StaffingPublisher mockPublisher;
    private StaffingServiceServer server;
    private HttpClient httpClient;
    private final int testPort = 7099;
    private final String baseUrl = "http://localhost:" + testPort;

    @BeforeEach
    void setUp() {
        mockClient = mock(StaffingServiceClient.class);
        mockPublisher = mock(StaffingPublisher.class);
        server = new StaffingServiceServer(mockClient, mockPublisher);
        server.start(testPort);
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testHealthEndpointReturnsOK() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
    }

    @Test
    void testScheduleReturns404IfWardDoesNotExist() throws Exception {
        when(mockClient.checkWardExists("W_UNKNOWN")).thenReturn(false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/schedule/W_UNKNOWN"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Ward not found"));
    }

    @Test
    void testScheduleReturnsStandardRotationForLowAlertLevel() throws Exception {
        when(mockClient.checkWardExists("W1")).thenReturn(true);
        when(mockClient.alertLevel()).thenReturn(1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/schedule/W1"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("STANDARD ROTATION"));
    }

    @Test
    void testPostStaffingReturns400ForEmptyBody() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/staffing"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Request body cannot be empty"));
        verifyNoInteractions(mockPublisher);
    }

    @Test
    void testPostStaffingPublishesUpdateAndReturns202() throws Exception {
        String payload = "{\"wardId\":\"W1\",\"shift\":\"NIGHT\",\"staffCount\":6}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/staffing"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode());
        assertTrue(response.body().contains("published succesfully"));
        verify(mockPublisher).publishUpdate(payload);
    }
}