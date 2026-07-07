package com.gym.crm.workload.model;

import com.gym.crm.workload.utils.TestDataProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerWorkloadValidationTest {
    private final TrainerWorkload workload = TestDataProvider.buildTrainerWorkload();
    private final YearWorkload yearWorkload = TestDataProvider.buildYearWorkload();
    private final MonthWorkload monthWorkload = TestDataProvider.buildMonthWorkload();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @MethodSource("invalidUsernames")
    void shouldFailValidationForInvalidUsername(String username) {
        TrainerWorkload invalidWorkload = workload.toBuilder().trainerUsername(username).build();

        Set<ConstraintViolation<TrainerWorkload>> violations = validator.validate(invalidWorkload);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .contains("trainerUsername");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldFailValidationForInvalidFirstName(String firstName) {
        TrainerWorkload invalidWorkload = workload.toBuilder().trainerFirstName(firstName).build();

        Set<ConstraintViolation<TrainerWorkload>> violations = validator.validate(invalidWorkload);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .contains("trainerFirstName");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldFailValidationForInvalidLastName(String lastName) {
        TrainerWorkload invalidWorkload = workload.toBuilder().trainerLastName(lastName).build();

        Set<ConstraintViolation<TrainerWorkload>> violations = validator.validate(invalidWorkload);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .contains("trainerLastName");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void shouldFailValidationForInvalidYear(int year) {
        YearWorkload invalidWorkload = yearWorkload.toBuilder().year(year).build();

        Set<ConstraintViolation<YearWorkload>> violations = validator.validate(invalidWorkload);

        assertThat(invalidWorkload.getMonths()).isNotNull();
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .contains("year");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 13, -1, 100})
    void shouldFailValidationForInvalidMonth(int month) {
        MonthWorkload invalidWorkload = monthWorkload.toBuilder().month(month).build();

        Set<ConstraintViolation<MonthWorkload>> violations = validator.validate(invalidWorkload);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .contains("month");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -60})
    void shouldFailValidationForInvalidTrainingDuration(int duration) {
        MonthWorkload invalidWorkload = monthWorkload.toBuilder().trainingDuration(duration).build();

        Set<ConstraintViolation<MonthWorkload>> violations = validator.validate(invalidWorkload);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .contains("trainingDuration");
    }

    @Test
    void shouldPassValidationForValidTrainerWorkload() {
        Set<ConstraintViolation<TrainerWorkload>> violations = validator.validate(workload);

        assertThat(violations).isEmpty();
    }

    private static Stream<String> invalidUsernames() {
        return Stream.of(null,
                "",
                "ab",
                "a".repeat(111));
    }
}