package co.wethinkcode.healthsafe;
import co.wethinkcode.healthsafe.client.ClientRequest;

import co.wethinkcode.healthsafe.mq.EquipmentFailurePublisher;
import co.wethinkcode.healthsafe.mq.WardMqHandler;
import co.wethinkcode.healthsafe.server.WardApiServer;
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
        EquipmentFailurePublisher failurePublisher = new EquipmentFailurePublisher();

        WardApiServer server = new WardApiServer(hospitalWards);




        // MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
        WardMqHandler mqHandler = new WardMqHandler();
        mqHandler.startListening();

// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.


        server.start(7031);

    }
}