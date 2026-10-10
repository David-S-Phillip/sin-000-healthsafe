package co.wethinkcode.healthsafe.server;
import co.wethinkcode.healthsafe.client.StaffingServiceClient;
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
    private final StaffingServiceClient clientResponse;

    public StaffingServiceServer() {
        this.app = Javalin.create();
        this.clientResponse = new StaffingServiceClient();
        setupRoutes();

    }

    private void setupRoutes() {
        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/schedule/{wardId}", ctx -> {
            String wardId = ctx.pathParam("wardId");

            // 1. Validate Ward
            boolean doesWardExist = clientResponse.checkWardExists(wardId);

            // If the ward doesn't exist (or the network crashed), bounce the request immediately
            if (!doesWardExist) {
                ctx.status(404).result("Ward not found or ward-service unavailable.");
                return;
            }

            // 2. Fetch Emergency Status
            int alertLevel = clientResponse.alertLevel();

            // If the alert level fetch failed (returned -1), we default to 0 to keep the hospital running
            if (alertLevel == -1) {
                alertLevel = 0;
            }

            // 3. Compute Schedule
            String scheduleSummary = computeSchedule(wardId, alertLevel);

            // 4. Return the computed schedule result as JSON (You deleted this!)
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
