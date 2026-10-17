package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class EquipmentFailureConsumer {

    private final ConnectionFactory connectionFactory;

    public EquipmentFailureConsumer() {
        this.connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    }

    public EquipmentFailureConsumer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void startListening() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Queue subscription for point-to-point delivery
            Queue queue = session.createQueue(MqConfig.QUEUE);
            MessageConsumer consumer = session.createConsumer(queue);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            System.out.println("[equipment-alert-service] CRITICAL ALERT RECEIVED: " + textMessage.getText());
                        }
                    } catch (JMSException e) {
                        System.err.println("Error processing failure alert: " + e.getMessage());
                    }
                }
            });

            System.out.println("[equipment-alert-service] Subscribed to queue: " + MqConfig.QUEUE);

        } catch (JMSException e) {
            System.err.println("Failed to start EquipmentFailureConsumer: " + e.getMessage());
        }
    }
}