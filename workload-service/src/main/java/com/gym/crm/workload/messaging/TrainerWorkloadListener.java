package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.exception.InvalidMessageException;
import com.gym.crm.workload.exception.WorkloadMessageProcessingException;
import com.gym.crm.workload.mapper.TrainerWorkloadMapper;
import com.gym.crm.workload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadListener {
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    private final TrainerWorkloadService trainerWorkloadService;
    private final TrainerWorkloadMapper mapper;
    private final TrainerWorkloadMessageValidator validator;
    private final TrainerWorkloadDlqSender dlqSender;

    @JmsListener(destination = "${spring.jms.trainer-workload.queue}", concurrency = "${spring.jms.trainer-workload.concurrency}")
    public void consume(TrainerWorkloadMessage message, @Header(name = TRACE_HEADER, required = false) String traceId) {
        String trId = traceId != null ? traceId : UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, trId);

        try {
            processMessage(message, trId);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }

    private void processMessage(TrainerWorkloadMessage message, String transactionId) {
        String trainerUsername = message.getTrainerUsername();

        try {
            log.info("Received workload update: trainer={}, action={}", trainerUsername, message.getActionType());

            validator.validate(message);
            trainerWorkloadService.update(mapper.toUpdateDTO(message));

            log.info("Workload updated successfully: trainer={}", trainerUsername);
        } catch (InvalidMessageException e) {
            log.warn("Invalid workload message received for trainer={}. Reason={}", trainerUsername, e.getMessage());

            sendToDlq(message, e.getMessage(), transactionId);
        } catch (RuntimeException e) {
            log.error("Unexpected error while processing workload update: trainer={}, action={}", trainerUsername, message.getActionType(), e);

            throw new WorkloadMessageProcessingException("Failed to process workload message", e);
        }
    }

    private void sendToDlq(TrainerWorkloadMessage message, String reason, String transactionId) {
        try {
            dlqSender.send(message, reason, transactionId);
        } catch (JmsException ex) {
            throw new WorkloadMessageProcessingException("Unable to send workload message to DLQ", ex);
        }
    }
}
