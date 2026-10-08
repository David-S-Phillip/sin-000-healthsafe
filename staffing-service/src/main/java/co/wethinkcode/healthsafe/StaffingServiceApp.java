package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.server.StaffingServiceServer;
import io.javalin.Javalin;

public class StaffingServiceApp {

    public static void main(String[] args) {
        StaffingServiceServer server = new StaffingServiceServer();
        server.start(7033);


        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
