package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class WardMqHandler {

    private final ConnectionFactory connectionFactory;

    // Default constructor connects to the local ActiveMQ broker URL
    public WardMqHandler() {
        this.connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    }

    // Overloaded constructor for unit testing with Mockito
    public WardMqHandler(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void startListening() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(MqConfig.TOPIC);

            MessageConsumer consumer = session.createConsumer(topic);

            // Asynchronous listener triggered whenever a message lands on the topic
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String payload = textMessage.getText();
                            System.out.println("[ward-service] Received staffing update: " + payload);
                        }
                    } catch (JMSException e) {
                        System.err.println("Error reading JMS message: " + e.getMessage());
                    }
                }
            });

            System.out.println("[ward-service] Subscribed to topic: " + MqConfig.TOPIC);

        } catch (JMSException e) {
            System.err.println("Failed to start WardMqHandler: " + e.getMessage());
        }
    }
}