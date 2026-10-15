package co.wethinkcode.healthsafe.mq;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StaffingPublisherTest {
    @Test
    void testPublishUpdateSendsMessageAndClosesResources() throws Exception {
        // 1. Mock all JMS infrastructure objects
        ConnectionFactory mockFactory = mock(ConnectionFactory.class);
        Connection mockConnection = mock(Connection.class);
        Session mockSession = mock(Session.class);
        Topic mockTopic = mock(Topic.class);
        MessageProducer mockProducer = mock(MessageProducer.class);
        TextMessage mockTextMessage = mock(TextMessage.class);

        // 2. Stub factory and session methods
        when(mockFactory.createConnection()).thenReturn(mockConnection);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createTopic(anyString())).thenReturn(mockTopic);
        when(mockSession.createProducer(mockTopic)).thenReturn(mockProducer);
        when(mockSession.createTextMessage(anyString())).thenReturn(mockTextMessage);

        // 3. Inject mockFactory using your overloaded constructor
        StaffingPublisher publisher = new StaffingPublisher(mockFactory);

        // 4. Call the method under test
        String payload = "{\"ward\":\"ICU\",\"status\":\"ALERT\"}";
        publisher.publishUpdate(payload);

        // 5. Verify expectations
        verify(mockSession).createTextMessage(payload);
        verify(mockProducer, times(1)).send(mockTextMessage);

        // Verify resource cleanup
        verify(mockProducer).close();
        verify(mockSession).close();
        verify(mockConnection).close();
    }

}