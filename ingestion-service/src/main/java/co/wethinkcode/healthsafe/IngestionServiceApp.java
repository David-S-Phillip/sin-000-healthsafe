package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) {
        // Create the separate server class
        IngestionServer server = new IngestionServer();

        // Start the server on port 7030
        server.start(7030);
    }
}
