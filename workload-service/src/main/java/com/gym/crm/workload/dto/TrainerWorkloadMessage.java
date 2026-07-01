package com.gym.crm.workload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
public class TrainerWorkloadMessage {
    @NotBlank(message = "Trainer username is required")
    @Size(max = 110, message = "Trainer username cannot exceed 110 characters")
    private String trainerUsername;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String trainerFirstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String trainerLastName;

    @NotNull(message = "Is active flag is required")
    private Boolean isActive;

    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Positive(message = "Training duration must be a positive number")
    private Integer trainingDuration;

    @NotNull(message = "Action type is required")
    private ActionType actionType;
}
