package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.exception.InvalidMessageException;
import com.gym.crm.workload.exception.WorkloadMessageProcessingException;
import com.gym.crm.workload.mapper.TrainerWorkloadMapper;
import com.gym.crm.workload.service.TrainerWorkloadService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
    @Mock
    private TrainerWorkloadMessageValidator validator;

    @InjectMocks
    private TrainerWorkloadListener listener;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void consume_shouldSetAndRemoveMdc_whenTraceIdPresent() {
        String traceId = "trace-123";
        doNothing().when(validator).validate(workloadMessage);
        when(mapper.toUpdateDTO(workloadMessage)).thenReturn(updateDTO);

        listener.consume(workloadMessage, traceId);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(validator).validate(workloadMessage);
        verify(trainerWorkloadService).update(updateDTO);
    }

    @Test
    void consume_shouldNotSetMdc_whenTraceIdIsNull() {
        doNothing().when(validator).validate(workloadMessage);
        when(mapper.toUpdateDTO(workloadMessage)).thenReturn(updateDTO);

        listener.consume(workloadMessage, null);

        assertThat(MDC.get(TRANSACTION_ID)).isNull();
        verify(validator).validate(workloadMessage);
        verify(trainerWorkloadService).update(updateDTO);
    }

    @Test
    void consume_shouldThrowWorkloadMessageProcessingException_whenServiceThrows() {
        String traceId = "trace-456";
        doNothing().when(validator).validate(workloadMessage);
        when(mapper.toUpdateDTO(workloadMessage)).thenReturn(updateDTO);
        doThrow(new RuntimeException("Test error")).when(trainerWorkloadService).update(updateDTO);

        WorkloadMessageProcessingException exception = assertThrows(WorkloadMessageProcessingException.class, () -> listener.consume(workloadMessage, traceId));

        assertThat(exception.getMessage()).isEqualTo("Failed to process workload message");
        assertThat(exception.getCause()).isInstanceOf(RuntimeException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("Test error");
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    @Test
    void consume_shouldThrowWorkloadMessageProcessingException_whenValidationFails() {
        String traceId = "trace-789";
        doThrow(new InvalidMessageException("Invalid!")).when(validator).validate(workloadMessage);

        WorkloadMessageProcessingException exception = assertThrows(WorkloadMessageProcessingException.class, () -> listener.consume(workloadMessage, traceId));

        assertThat(exception.getMessage()).isEqualTo("Failed to process workload message");
        assertThat(exception.getCause()).isInstanceOf(InvalidMessageException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("Invalid!");
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
