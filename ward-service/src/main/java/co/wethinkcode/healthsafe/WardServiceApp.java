package co.wethinkcode.healthsafe;
import co.wethinkcode.healthsafe.client.ClientRequest;

import io.javalin.Javalin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.ArrayList;

public class WardServiceApp {

    public static void main(String[] args) {
        ClientRequest csvInformation = new ClientRequest("http://localhost:7030/wards");
        List<WardRecord> hospitalWards = csvInformation.fetchWards();
//        System.out.println("Loaded " + hospitalWards.size() + " wards.");


        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.
        app.get("/wards", ctx -> {
            ctx.json(hospitalWards);
        });

        app.get("/wards/{id}", ctx -> {
            String targetId = ctx.pathParam("id");

            WardRecord foundWard = hospitalWards.stream()
                    .filter(ward -> targetId.equals(ward.getWardId()))
                    .findFirst()
                    .orElse(null);

            if (foundWard != null){
                ctx.json(foundWard);
            }else{
                ctx.status(404).result("Ward not found");
            }
        });

        app.get("/departments", ctx -> {
            List<String> departments = hospitalWards.stream()
                    .map(WardRecord::getDepartment)
                    .distinct()
                    .toList();

                    ctx.json(departments);
        });
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
