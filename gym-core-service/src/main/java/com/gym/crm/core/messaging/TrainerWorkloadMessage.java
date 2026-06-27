package com.gym.crm.core.messaging;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
public class TrainerWorkloadMessage {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private ActionType actionType;
}
