package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerWorkloadDlqSender {
    private static final String FAILURE_REASON = "failureReason";
    private static final String TRACE_HEADER = "X-Trace-Id";

    private final JmsTemplate jmsTemplate;
    private final String dlq;

   public TrainerWorkloadDlqSender(JmsTemplate jmsTemplate,
                                   @Value("${spring.jms.trainer-workload.dlq}") String dlq) {
       this.jmsTemplate = jmsTemplate;
        this.dlq = dlq;
   }

    public void send(TrainerWorkloadMessage message, String reason, String traceId) {
        jmsTemplate.convertAndSend(dlq, message, jmsMessage -> {
            jmsMessage.setStringProperty(FAILURE_REASON, reason);

            if (traceId != null) {
                jmsMessage.setStringProperty(TRACE_HEADER, traceId);
            }

            return jmsMessage;
        });

        log.warn("Message published to DLQ: trainer={}, reason={}", message.getTrainerUsername(), reason);
    }
}
