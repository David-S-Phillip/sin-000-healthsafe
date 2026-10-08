package co.wethinkcode.healthsafe.server;
import io.javalin.Javalin;
import java.util.Map;

public class AlertLevelServiceServer {
    private final Javalin app;
    private int currentAlertLevel = 0;

    public AlertLevelServiceServer(){
        this.app = Javalin.create();
        setupRoutes();
    }

    // this configures the responses to the HTTP Requests
    private void setupRoutes() {
        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/alert-level", ctx -> {
            ctx.json(Map.of("level", currentAlertLevel));
        });

        app.post("/alert-level/{level}", ctx -> {
            try {
                int newLevel = Integer.parseInt(ctx.pathParam("level"));
                if (newLevel >= 0 && newLevel <= 8) {
                    this.currentAlertLevel = newLevel;
                    ctx.status(200).result("Alert level set to: " + currentAlertLevel);
                } else {
                    ctx.status(400).result("Error: Alert level must be between 0 and 8.");
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Error: Level must be an integer.");
            }
        });
    }

    // used to start the javalin server
    public void start(int port) {
        this.app.start(port);
    }

    // used to stop the server
    public void stop() {
        this.app.stop();
    }
}
