package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.mapper.TrainerWorkloadMapper;
import com.gym.crm.workload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadListener {
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String TRANSACTION_ID = "transactionId";

    private final TrainerWorkloadService trainerWorkloadService;
    private final TrainerWorkloadMapper mapper;

    @JmsListener(destination = "${spring.jms.queue.trainer-workload}")
    public void consume(TrainerWorkloadMessage message, @Header(name = TRACE_HEADER, required = false) String traceId) {
        if (traceId != null) {
            MDC.put(TRANSACTION_ID, traceId);
        }

        try {
            trainerWorkloadService.update(mapper.toUpdateDTO(message));
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}
