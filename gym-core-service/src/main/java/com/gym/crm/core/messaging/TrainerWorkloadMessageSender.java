package com.gym.crm.core.messaging;

import com.gym.crm.core.exception.ServiceConnectionException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessageSender {
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    private final JmsTemplate template;
    private final MessageConverter converter;

    @Value("${spring.jms.queue.trainer-workload}")
    private String workloadQueue;

    public void sendUpdate(TrainerWorkloadMessage message) {
        try {
            template.send(workloadQueue, session -> {
                Message msg = converter.toMessage(message, session);
                msg.setStringProperty(TRACE_HEADER, MDC.get(TRANSACTION_ID));

                return msg;
            });

            log.info("Workload update message sent: trainer={}, action={}", message.getTrainerUsername(), message.getActionType().name());
        } catch (JmsException ex) {
            log.error("Failed to send workload update to queue: trainer={}, action={}", message.getTrainerUsername(), message.getActionType(), ex);

            throw new ServiceConnectionException("Failed to send message to workload queue");
        }
    }
}
