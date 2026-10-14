package co.wethinkcode.healthsafe.mq;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class StaffingPublisher {

    public void publishUpdate(String jsonPayload) {

        try {
            // we are gonna connect to the ActiveMq server running in the docker container
            ConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            // open a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Find the specific topic inside the broker (That topic inside the ActiveMq running in the docker container )
            Destination topic = session.createTopic(MqConfig.TOPIC);
            MessageProducer producer = session.createProducer(topic);

            // Send the payload and fokkof!
            TextMessage message = session.createTextMessage(jsonPayload);
            producer.send(message);

            System.out.println("Published staffing update to topic!");

            // clean up
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e){
            System.err.println("Failed to publish message: " + e.getMessage());
        }
    }

    // helper methods (Each one does a job and throws a JMSException up)

//    private Connection createAndStartConnection() throws JMSException{
//        Connection connection =
//    }


}
