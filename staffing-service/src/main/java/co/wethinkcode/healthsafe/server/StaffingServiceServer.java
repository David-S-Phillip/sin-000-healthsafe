package co.wethinkcode.healthsafe.server;
import com.fasterxml.jackson.core.type.TypeReference;
import io.javalin.Javalin;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.wethinkcode.healthsafe.mq.MqConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class StaffingServiceServer {
    private final Javalin app;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public StaffingServiceServer() {
        this.app = Javalin.create();
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        setupRoutes();
    }

    private void setupRoutes() {
        // Health check required by README
        app.get("/health", ctx -> ctx.result("OK"));

        // Main scheduling endpoint
        app.get("/schedule/{wardId}", ctx -> {
            String wardId = ctx.pathParam("wardId");

            // 1. Validate Ward via ward-service (Port 7031)
            try {
                HttpRequest wardRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:7031/wards/" + wardId))
                        .GET()
                        .build();

                HttpResponse<String> wardResponse = httpClient.send(wardRequest, HttpResponse.BodyHandlers.ofString());

                if (wardResponse.statusCode() == 404) {
                    ctx.status(404).result("Ward not found: " + wardId);
                    return;
                } else if (wardResponse.statusCode() != 200) {
                    ctx.status(502).result("Error communicating with ward-service");
                    return;
                }
            } catch (Exception e) {
                ctx.status(503).result("Ward service unavailable");
                return;
            }

            // 2. Fetch Emergency Status via alert-level-service (Port 7032)
            int alertLevel = 0;
            try {
                HttpRequest alertRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:7032/alert-level"))
                        .GET()
                        .build();

                HttpResponse<String> alertResponse = httpClient.send(alertRequest, HttpResponse.BodyHandlers.ofString());

                if (alertResponse.statusCode() == 200) {
                    // Parse JSON: { "level": X }
                    Map<String, Object> alertJson = objectMapper.readValue(alertResponse.body(), new TypeReference<Map<String, Object>>() {});
                    if (alertJson.containsKey("level") && alertJson.get("level") instanceof Number) {
                        alertLevel = ((Number) alertJson.get("level")).intValue();
                    }
                }
            } catch (Exception e) {
                // Fallback or default alert level if service lags
                alertLevel = 0;
            }

            // 3. Compute Schedule based on ward and alert level
            String scheduleSummary = computeSchedule(wardId, alertLevel);

            // 4. Publish to ActiveMQ topic (staffing-events-topic) if MQ config is present
            // (You can wire up your MqConfig publisher here for Stage 3)

            // Return the computed schedule result
            ctx.json(Map.of(
                    "wardId", wardId,
                    "alertLevel", alertLevel,
                    "schedule", scheduleSummary
            ));
        });
    }

    private String computeSchedule(String wardId, int alertLevel) {
        if (alertLevel >= 6) {
            return "CRITICAL CODE BLUE: Triple staffing required for Ward " + wardId;
        } else if (alertLevel >= 3) {
            return "ELEVATED STATUS: Double staffing required for Ward " + wardId;
        } else {
            return "STANDARD ROTATION: Normal on-call schedule for Ward " + wardId;
        }
    }

    public void start(int port) {
        this.app.start(port);
    }

    public void stop() {
        this.app.stop();
    }
}
