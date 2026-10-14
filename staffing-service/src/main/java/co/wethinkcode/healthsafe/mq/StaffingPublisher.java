package co.wethinkcode.healthsafe.mq;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class StaffingPublisher {

    private final ConnectionFactory connectionFactory;

    public StaffingPublisher(){
        this.connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    }

    public void publishUpdate(String jsonPayload) {

        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            MessageProducer producer = createProducer(session);

            sendMessage(session, producer, jsonPayload);
            System.out.println("Published staffing update to topic!");

            closeResources(producer, session, connection);
        } catch (JMSException e){
            System.err.println("Failed to publish message: " + e.getMessage());
        }
    }

    // helper methods (Each one does a job and throws a JMSException up)

    private Connection createAndStartConnection() throws JMSException{
        Connection connection = connectionFactory.createConnection();
        connection.start();
        return connection;
    }

    private Session createSession(Connection connection) throws JMSException{
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private MessageProducer createProducer(Session session) throws JMSException{
        Destination topic = session.createTopic(MqConfig.TOPIC);
        return session.createProducer(topic);
    }

    private void sendMessage(Session session, MessageProducer producer, String payload) throws JMSException {
        TextMessage message = session.createTextMessage(payload);
        producer.send(message);
    }

    private void closeResources(MessageProducer producer, Session session, Connection connection ){
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            System.err.println("ERROR closing JMS resources " + e.getMessage());
        }
    }



        public static void main(String[] args){
            StaffingPublisher publisher = new StaffingPublisher();
            publisher.publishUpdate("Bruh its not looking good");
        }

}
