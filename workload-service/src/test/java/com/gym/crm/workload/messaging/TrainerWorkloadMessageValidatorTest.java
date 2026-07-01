package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.exception.InvalidMessageException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
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
    private static final int INVALID_DURATION = 0;

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private final TrainerWorkloadMessage workloadMessage = buildTrainerWorkloadMessage();

    private TrainerWorkloadMessageValidator sut;

    @BeforeEach
    void setUp() {
        sut = new TrainerWorkloadMessageValidator(VALIDATOR);
    }

    @Test
    void validate_shouldThrow_whenMessageIsNull() {
        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(null));

        assertThat(exception.getMessage()).isEqualTo("Message cannot be null");
    }

    @Test
    void validate_shouldThrow_whenTrainerUsernameMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainerUsername(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Trainer username is required");
    }

    @Test
    void validate_shouldThrow_whenTrainerFirstNameMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainerFirstName("").build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("First name is required");
    }

    @Test
    void validate_shouldThrow_whenTrainerLastNameMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainerLastName(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Last name is required");
    }

    @Test
    void validate_shouldThrow_whenIsActiveMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().isActive(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Is active flag is required");
    }

    @Test
    void validate_shouldThrow_whenActionTypeMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().actionType(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Action type is required");
    }

    @Test
    void validate_shouldThrow_whenTrainingDateMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainingDate(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Training date is required");
    }

    @Test
    void validate_shouldThrow_whenTrainingDurationMissing() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainingDuration(null).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Training duration is required");
    }

    @Test
    void validate_shouldThrow_whenTrainingDurationIsNotPositive() {
        TrainerWorkloadMessage msg = workloadMessage.toBuilder().trainingDuration(0).build();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(msg));

        assertThat(exception.getMessage()).contains("Training duration must be a positive number");
    }

    @Test
    void validate_shouldNotThrow_whenMessageIsValid() {
        assertDoesNotThrow(() -> sut.validate(workloadMessage));
    }

    @Test
    void validate_shouldConcatenateValidationMessages_whenMultipleFieldsAreInvalid() {
        TrainerWorkloadMessage messageWithMissingFields = buildTrainerWorkloadMessageWithMissingFields();

        InvalidMessageException exception = assertThrows(InvalidMessageException.class, () -> sut.validate(messageWithMissingFields));

        assertThat(exception.getMessage())
                .contains("Trainer username is required")
                .contains("First name is required")
                .contains("Training duration must be a positive number");
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

    private TrainerWorkloadMessage buildTrainerWorkloadMessageWithMissingFields() {
        return TrainerWorkloadMessage.builder()
                .trainerLastName(LAST_NAME)
                .isActive(true)
                .trainingDate(DATE)
                .actionType(ActionType.ADD)
                .trainingDuration(INVALID_DURATION)
                .build();
    }
}
