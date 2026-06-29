package com.gym.crm.core.messaging;

import com.gym.crm.core.exception.ServiceConnectionException;
import com.gym.crm.core.utils.TestDataProvider;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadMessageSenderTest {
    private final static String QUEUE_NAME = "trainer.workload.queue";
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    private final TrainerWorkloadMessage workloadMessage = TestDataProvider.buildTrainerWorkloadMessage(ActionType.ADD);

    @Mock
    private JmsTemplate template;

    @Mock
    private MessageConverter converter;

    @Mock
    private Session session;

    @Mock
    private Message message;

    @InjectMocks
    private TrainerWorkloadMessageSender sender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sender, "workloadQueue", QUEUE_NAME);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void send_shouldSendMessageToCorrectQueue() throws Exception {
        when(converter.toMessage(workloadMessage, session)).thenReturn(message);

        sender.sendUpdate(workloadMessage);

        ArgumentCaptor<MessageCreator> captor = ArgumentCaptor.forClass(MessageCreator.class);
        verify(template).send(eq(QUEUE_NAME), captor.capture());
        captor.getValue().createMessage(session);
        verify(converter).toMessage(workloadMessage, session);
    }

    @Test
    void send_shouldSetTransactionIdProperty_whenPresentInMdc() throws Exception {
        MDC.put(TRANSACTION_ID, "tx-123");
        when(converter.toMessage(workloadMessage, session)).thenReturn(message);

        sender.sendUpdate(workloadMessage);

        ArgumentCaptor<MessageCreator> captor = ArgumentCaptor.forClass(MessageCreator.class);
        verify(template).send(eq(QUEUE_NAME), captor.capture());
        captor.getValue().createMessage(session);
        verify(message).setStringProperty(TRACE_HEADER, "tx-123");
    }

    @Test
    void send_shouldNotSetTransactionIdProperty_whenMdcIsEmpty() throws Exception {
        MDC.remove(TRANSACTION_ID);
        when(converter.toMessage(workloadMessage, session)).thenReturn(message);

        sender.sendUpdate(workloadMessage);

        ArgumentCaptor<MessageCreator> captor = ArgumentCaptor.forClass(MessageCreator.class);
        verify(template).send(eq(QUEUE_NAME), captor.capture());
        captor.getValue().createMessage(session);
        verify(message, never()).setStringProperty(eq(TRANSACTION_ID), any());
    }

    @Test
    void send_shouldThrowServiceConnectionException_onJmsException() {
        doThrow(new org.springframework.jms.JmsException("JMS error") {}).when(template).send(eq(QUEUE_NAME), any());

        ServiceConnectionException exception = assertThrows(ServiceConnectionException.class, () ->  sender.sendUpdate(workloadMessage));

        assertThat(exception.getMessage()).isEqualTo("Failed to send message to workload queue");
    }
}
