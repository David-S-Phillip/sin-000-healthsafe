package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

import java.util.Map;

public class AlertLevelServiceApp {
    private static int currentAlertLevel = 0;

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));

        //GET /alert-level (0 - 8)
        app.get("/alert-level", ctx -> {
            ctx.json(Map.of("level", currentAlertLevel));
        });

        app.post("/alert-level/{level}", ctx -> {
            try {
                int newLevel = Integer.parseInt(ctx.pathParam("level"));
                if (newLevel >= 0 && newLevel <= 8) {
                    currentAlertLevel = newLevel;
                    ctx.status(200).result("Alert level set to: " + currentAlertLevel);
                } else {
                    ctx.status(400).result("Error: Alert level must be between 0 and 8.");
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Error: Level must be an integer.");
            }
        });

        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }
}
