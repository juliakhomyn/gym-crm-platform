package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.mapper.TrainerWorkloadMapper;
import com.gym.crm.workload.service.TrainerWorkloadService;
import com.gym.crm.workload.utils.TestDataProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DATE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadListenerTest {
    private static final String TRANSACTION_ID = "transactionId";
    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";
    private static final int DURATION = 60;
    private static final LocalDate DATE = LocalDate.of(2020, Month.JANUARY, 1);

    private final TrainerWorkloadMessage workloadMessage = buildTrainerWorkloadMessage();
    private final TrainerWorkloadUpdateDTO updateDTO = buildUpdateDTO();

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private TrainerWorkloadMapper mapper;

    @InjectMocks
    private TrainerWorkloadListener listener;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void consume_shouldSetAndRemoveMdc_whenTraceIdPresent() {
        String traceId = "trace-123";
        when(mapper.toUpdateDTO(workloadMessage)).thenReturn(updateDTO);

        listener.consume(workloadMessage, traceId);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(trainerWorkloadService).update(updateDTO);
    }

    @Test
    void consume_shouldNotSetMdc_whenTraceIdIsNull() {
        when(mapper.toUpdateDTO(workloadMessage)).thenReturn(updateDTO);

        listener.consume(workloadMessage, null);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(trainerWorkloadService).update(updateDTO);
    }

    @Test
    void consume_shouldRemoveMdcEvenOnException() {
        String traceId = "trace-456";
        when(mapper.toUpdateDTO(workloadMessage)).thenReturn(updateDTO);
        doThrow(new RuntimeException("Test error")).when(trainerWorkloadService).update(updateDTO);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> listener.consume(workloadMessage, traceId));

        assertThat(exception.getMessage()).isEqualTo("Test error");
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

    private TrainerWorkloadUpdateDTO buildUpdateDTO() {
        return TrainerWorkloadUpdateDTO.builder()
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
