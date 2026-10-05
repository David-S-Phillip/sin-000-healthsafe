package co.wethinkcode.healthsafe.server;

import co.wethinkcode.healthsafe.WardRecord;
import io.javalin.Javalin;

import java.util.List;

public class WardApiServer {
    private final Javalin app;
    private final List<WardRecord> hospitalWards;

    public WardApiServer(List<WardRecord> hospitalWards){
        this.hospitalWards = hospitalWards;
        this.app = Javalin.create();
        setupRoutes();
    }

    private void setupRoutes(){
        // API endpoint for /health
        app.get("/health", ctx -> ctx.result("OK"));

        // API endpoint for /wards
        app.get("/wards",ctx -> {
            ctx.json(hospitalWards);
        });

        // Api endpoint for /wards/id can search for a specific ward via there id
        app.get("/wards/{id}", ctx -> {
            String targetId = ctx.pathParam("id");
            WardRecord foundWard = hospitalWards.stream()
                    .filter(ward -> targetId.equals(ward.getWardId()))
                    .findFirst()
                    .orElse(null);

            if (foundWard != null) {
                ctx.json(foundWard);
            } else {
                ctx.status(404).result("Ward not found");
            }
        });
        // Api endpoint for /departments, shows all the departments in the hospital
        app.get("/departments", ctx -> {
            List<String> departments = hospitalWards.stream()
                    .map(WardRecord::getDepartment)
                    .distinct()
                    .toList();
            ctx.json(departments);
        });

    }

    public void start(int port){
        app.start(port);
    }

    public void stop(){
        app.stop();
    }

    // Getter method for port number, using in unit testing
    public int port(){
        return app.port();
    }
}
