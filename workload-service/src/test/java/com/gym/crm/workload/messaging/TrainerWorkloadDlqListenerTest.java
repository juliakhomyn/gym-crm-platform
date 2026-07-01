package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadDlqListenerTest {
    private static final String TRANSACTION_ID = "transactionId";
    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";
    private static final int DURATION = 60;
    private static final LocalDate DATE = LocalDate.of(2020, Month.JANUARY, 1);

    private final TrainerWorkloadMessage workloadMessage = buildTrainerWorkloadMessage();

    private final TrainerWorkloadDlqListener listener = new TrainerWorkloadDlqListener();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void consume_shouldSetAndRemoveMdc_whenTraceIdPresent() {
        String traceId = "trace-123";

        listener.consume(workloadMessage, "Validation failed", traceId);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    @Test
    void consume_shouldNotLeaveMdc_whenTraceIdIsNull() {
        listener.consume(workloadMessage, "Validation failed", null);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    private TrainerWorkloadMessage buildTrainerWorkloadMessage() {
        return TrainerWorkloadMessage.builder()
                .trainerUsername(USERNAME)
                .trainerFirstName(FIRST_NAME)
                .trainerLastName(LAST_NAME)
                .isActive(true)
                .trainingDate(DATE)
                .trainingDuration(DURATION)
                .actionType(ActionType.ADD)
                .build();
    }
}
