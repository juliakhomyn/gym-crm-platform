package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.exception.InvalidMessageException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainerWorkloadMessageValidatorTest {

    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";
    private static final int DURATION = 60;
    private static final LocalDate DATE = LocalDate.of(2020, Month.JANUARY, 1);

    private final TrainerWorkloadMessage workloadMessage = buildTrainerWorkloadMessage();

    private final TrainerWorkloadMessageValidator validator = new TrainerWorkloadMessageValidator();

    @Test
    void validate_shouldThrow_whenMessageIsNull() {
        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(null));

        assertThat(exception.getMessage()).contains("Message cannot be null");
    }

    @Test
    void validate_shouldThrow_whenTrainerUsernameMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainerUsername(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Trainer username is missing");
    }

    @Test
    void validate_shouldThrow_whenTrainerFirstNameMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainerFirstName("").build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Trainer first name is missing");
    }

    @Test
    void validate_shouldThrow_whenTrainerLastNameMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainerLastName(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Trainer last name is missing");
    }

    @Test
    void validate_shouldThrow_whenIsActiveMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().isActive(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Is active flag is missing");
    }

    @Test
    void validate_shouldThrow_whenActionTypeMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().actionType(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Action type is missing");
    }

    @Test
    void validate_shouldThrow_whenTrainingDateMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainingDate(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Training date is missing");
    }

    @Test
    void validate_shouldThrow_whenTrainingDurationInvalid() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainingDuration(0).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> validator.validate(msg));

        assertThat(exception.getMessage()).contains("Training duration is missing or invalid");
    }

    @Test
    void validate_shouldNotThrow_whenMessageIsValid() {
        assertDoesNotThrow(() -> validator.validate(workloadMessage));
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
