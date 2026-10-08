package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.server.AlertLevelServiceServer;
import io.javalin.Javalin;

import java.util.Map;

public class AlertLevelServiceApp {
    private static int currentAlertLevel = 0;

    public static void main(String[] args) {
        AlertLevelServiceServer server = new AlertLevelServiceServer();
        server.start(7032);


        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }
}
