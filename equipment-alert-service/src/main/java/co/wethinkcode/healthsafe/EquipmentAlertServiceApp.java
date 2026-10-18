package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.EquipmentFailureConsumer;
import io.javalin.Javalin;

public class EquipmentAlertServiceApp {

    public static void main(String[] args) {
        // 1. Start listening to equipment-failure-queue
        EquipmentFailureConsumer consumer = new EquipmentFailureConsumer();
        consumer.startListening();

        // 2. Start Javalin health check server on port 7034
        Javalin app = Javalin.create().start(7034);
        app.get("/health", ctx -> ctx.result("OK"));

        System.out.println("[equipment-alert-service] Running on http://localhost:7034");
        // TODO (Uses a Queue to guarantee delivery of critical medical equipment failure alerts.)
        // Mechanism: ActiveMQ Queue (guaranteed delivery)
    }
}

// MQ TODO: consumes ActiveMQ queue MqConfig.QUEUE at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// Producer: ward-service publishes here when it detects an equipment failure on one of its wards.
