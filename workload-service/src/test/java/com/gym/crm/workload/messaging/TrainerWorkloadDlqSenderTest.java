package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadDlqSenderTest {
    private static final String DLQ = "trainer.workload.dlq";
    private static final String FAILURE_REASON = "failureReason";
    private static final String TRACE_HEADER = "X-Trace-Id";

    private static final String USERNAME = "Callum.Whitfield";

    private final TrainerWorkloadMessage message = buildMessage();

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Message jmsMessage;

    private TrainerWorkloadDlqSender sender;

    @BeforeEach
    void setUp() {
        sender = new TrainerWorkloadDlqSender(jmsTemplate, DLQ);
    }

    @Test
    void send_shouldPublishMessageToDlq() {
        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        sender.send(message, "Validation failed", "trace-123");

        verify(jmsTemplate).convertAndSend(eq(DLQ), eq(message), captor.capture());
    }

    @Test
    void send_shouldSetFailureReasonAndTraceIdHeaders() throws JMSException {
        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        sender.send(message, "Validation failed", "trace-123");

        verify(jmsTemplate).convertAndSend(eq(DLQ), eq(message), captor.capture());
        captor.getValue().postProcessMessage(jmsMessage);
        verify(jmsMessage).setStringProperty(FAILURE_REASON, "Validation failed");
        verify(jmsMessage).setStringProperty(TRACE_HEADER, "trace-123");
    }

    @Test
    void send_shouldNotSetTraceIdHeader_whenTraceIdIsNull() throws JMSException {
        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        sender.send(message, "Validation failed", null);

        verify(jmsTemplate).convertAndSend(eq(DLQ), eq(message), captor.capture());
        captor.getValue().postProcessMessage(jmsMessage);
        verify(jmsMessage).setStringProperty(FAILURE_REASON, "Validation failed");
        verify(jmsMessage, never()).setStringProperty(eq(TRACE_HEADER), anyString());
    }

    private TrainerWorkloadMessage buildMessage() {
        return TrainerWorkloadMessage.builder()
                .trainerUsername(USERNAME)
                .build();
    }
}
