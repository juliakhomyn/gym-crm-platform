package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerWorkloadDlqListener {

    private static final String FAILURE_REASON = "failureReason";
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    @JmsListener(destination = "${spring.jms.trainer-workload.dlq}")
    public void consume(TrainerWorkloadMessage message,
                        @Header(name = FAILURE_REASON, required = false) String reason,
                        @Header(name = TRACE_HEADER, required = false) String traceId) {
        if (traceId != null) {
            MDC.put(TRANSACTION_ID, traceId);
        }

        try {
            log.error("Received workload message from DLQ. Trainer={}, action={}, reason={}, message={}",
                    message.getTrainerUsername(), message.getActionType(), reason, message);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}