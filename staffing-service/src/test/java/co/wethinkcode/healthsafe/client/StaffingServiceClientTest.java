package co.wethinkcode.healthsafe.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StaffingServiceClientTest {

    private HttpClient httpClient;
    private HttpResponse<String> httpResponse;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCheckWardExistsReturnsTrueFor200() throws Exception {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        StaffingServiceClient client = new StaffingServiceClient(httpClient);

        assertTrue(client.checkWardExists("W1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCheckWardExistsReturnsFalseFor404() throws Exception {
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        StaffingServiceClient client = new StaffingServiceClient(httpClient);

        assertFalse(client.checkWardExists("UNKNOWN"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAlertLevelParsesJsonSuccessfully() throws Exception {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"level\": 3}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        StaffingServiceClient client = new StaffingServiceClient(httpClient);

        assertEquals(3, client.alertLevel());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAlertLevelReturnsMinusOneOnServerError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        StaffingServiceClient client = new StaffingServiceClient(httpClient);

        assertEquals(-1, client.alertLevel());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAlertLevelReturnsMinusOneOnNetworkException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        StaffingServiceClient client = new StaffingServiceClient(httpClient);

        assertEquals(-1, client.alertLevel());
    }
}