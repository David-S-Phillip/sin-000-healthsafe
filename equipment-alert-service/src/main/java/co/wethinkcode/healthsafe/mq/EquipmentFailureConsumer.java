package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class EquipmentFailureConsumer {

    private final ConnectionFactory connectionFactory;

    // Default constructor connects to local ActiveMQ broker URL
    public EquipmentFailureConsumer() {
        this.connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    }

    // Overloaded constructor for unit testing with Mockito
    public EquipmentFailureConsumer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void startListening() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Connects to point-to-point Queue
            Queue queue = session.createQueue(MqConfig.QUEUE);
            MessageConsumer consumer = session.createConsumer(queue);

            // Async listener triggered whenever a failure alert lands on the queue
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String payload = textMessage.getText();
                            System.out.println("[equipment-alert-service] CRITICAL ALERT RECEIVED: " + payload);
                        }
                    } catch (JMSException e) {
                        System.err.println("Error reading failure alert: " + e.getMessage());
                    }
                }
            });

            System.out.println("[equipment-alert-service] Subscribed to queue: " + MqConfig.QUEUE);

        } catch (JMSException e) {
            System.err.println("Failed to start EquipmentFailureConsumer: " + e.getMessage());
        }
    }
}