package co.wethinkcode.healthsafe.mq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WardMqHandlerTest {

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageConsumer consumer;

    @BeforeEach
    void setUp() throws JMSException {
        connectionFactory = mock(ConnectionFactory.class);
        connection = mock(Connection.class);
        session = mock(Session.class);
        topic = mock(Topic.class);
        consumer = mock(MessageConsumer.class);

        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic(any())).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(consumer);
    }

    @Test
    void testStartListeningSubscribesToTopicAndAttachesListener() throws JMSException {
        WardMqHandler handler = new WardMqHandler(connectionFactory);
        handler.startListening();

        verify(connection).start();
        verify(session).createTopic(MqConfig.TOPIC);
        verify(session).createConsumer(topic);
        verify(consumer).setMessageListener(any(MessageListener.class));
    }

    @Test
    void testMessageListenerProcessesTextMessage() throws JMSException {
        WardMqHandler handler = new WardMqHandler(connectionFactory);
        handler.startListening();

        // Capture the listener passed into consumer.setMessageListener()
        ArgumentCaptor<MessageListener> listenerCaptor = ArgumentCaptor.forClass(MessageListener.class);
        verify(consumer).setMessageListener(listenerCaptor.capture());

        MessageListener listener = listenerCaptor.getValue();

        // Mock an incoming TextMessage
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn("Staffing updated: 5 nurses assigned");

        // Verify onMessage runs without throwing an exception
        assertDoesNotThrow(() -> listener.onMessage(textMessage));
        verify(textMessage).getText();
    }

    @Test
    void testStartListeningHandlesJMSExceptionGracefully() throws JMSException {
        when(connectionFactory.createConnection()).thenThrow(new JMSException("Broker unreachable"));

        WardMqHandler handler = new WardMqHandler(connectionFactory);

        // Verify that startListening catches the JMSException without crashing the app
        assertDoesNotThrow(handler::startListening);
    }
}