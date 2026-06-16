package com.gym.crm.workload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrainerWorkload {
    private final String trainerUsername;
    private final String trainerFirstName;
    private final String trainerLastName;
    private final Boolean isActive;
    private final List<YearWorkload> years;
}
