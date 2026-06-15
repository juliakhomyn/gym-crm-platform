package com.gym.crm.core.facade.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TrainingRequestDTO {
    @NotBlank(message = "Trainee username is required")
    @Size(max = 110, message = "Trainee username cannot exceed 110 characters")
    private final String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    @Size(max = 110, message = "Trainer username cannot exceed 110 characters")
    private final String trainerUsername;

    @NotBlank(message = "Training name is required")
    @Size(max = 100, message = "Training name cannot exceed 100 characters")
    private final String trainingName;

    @NotNull(message = "Training date is required")
    private final LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Positive(message = "Training duration must be a positive number")
    private final int trainingDuration;
}
