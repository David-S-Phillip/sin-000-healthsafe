package co.wethinkcode.healthsafe.mq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jms.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EquipmentFailurePublisherTest {

    // 1. Declare mock dependencies
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Queue queue;
    private MessageProducer producer;
    private TextMessage textMessage;

    @BeforeEach
    void setUp() throws JMSException {
        // 2. Initialize mocks for each link in the JMS chain
        connectionFactory = mock(ConnectionFactory.class);
        connection = mock(Connection.class);
        session = mock(Session.class);
        queue = mock(Queue.class);
        producer = mock(MessageProducer.class);
        textMessage = mock(TextMessage.class);

        // 3. Chain mock behaviors so calling methods on one returns the next mock
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createQueue(MqConfig.QUEUE)).thenReturn(queue);
        when(session.createProducer(queue)).thenReturn(producer);
        when(session.createTextMessage(anyString())).thenReturn(textMessage);
    }

    @Test
    void testSendFailureAlertPublishesMessageAndClosesResources() throws JMSException {
        String testPayload = "{\"wardId\":\"W1\",\"equipment\":\"Ventilator\"}";

        // Inject mock connection factory via the overloaded constructor
        EquipmentFailurePublisher publisher = new EquipmentFailurePublisher(connectionFactory);

        // Execute the method under test
        publisher.sendFailureAlert(testPayload);

        // Verify the JMS pipeline behavior
        verify(connection).start();
        verify(session).createQueue(MqConfig.QUEUE);
        verify(session).createTextMessage(testPayload);
        verify(producer).send(textMessage);

        // Verify cleanup
        verify(producer).close();
        verify(session).close();
        verify(connection).close();
    }

    @Test
    void testSendFailureAlertHandlesJMSExceptionGracefully() throws JMSException {
        // Simulate ActiveMQ broker being offline
        when(connectionFactory.createConnection()).thenThrow(new JMSException("Broker down"));

        EquipmentFailurePublisher publisher = new EquipmentFailurePublisher(connectionFactory);

        // Verify exception is caught internally and doesn't crash the application
        assertDoesNotThrow(() -> publisher.sendFailureAlert("{\"test\":\"payload\"}"));
    }
}