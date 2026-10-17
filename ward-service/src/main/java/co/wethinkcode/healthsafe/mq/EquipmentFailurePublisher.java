package co.wethinkcode.healthsafe.mq;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class EquipmentFailurePublisher {

    private final ConnectionFactory connectionFactory;

    // Default constructor uses the broker URL defined in the MqConfig class
    public EquipmentFailurePublisher(){
        this.connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

    }

    // A overloaded constructor for tests with Mockito
    public EquipmentFailurePublisher(ConnectionFactory connectionFactory){
        this.connectionFactory = connectionFactory;
    }

    public void sendFailureAlert(String jsonPayload){
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Connect specifically to the QUEUE name defined in MqConfig
            Queue queue = session.createQueue(MqConfig.QUEUE);
            MessageProducer producer = session.createProducer(queue);

            TextMessage message = session.createTextMessage(jsonPayload);
            producer.send(message);

            System.out.println("[ward-service] Published equipment failure alert: " + jsonPayload);

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            System.err.println("Failed to send queue message: " + e.getMessage());

        }
    }
}
