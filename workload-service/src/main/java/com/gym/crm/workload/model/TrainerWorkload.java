package com.gym.crm.workload.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class TrainerWorkload {
    private final String trainerUsername;
    private final String trainerFirstName;
    private final String trainerLastName;
    private final Boolean isActive;
    @Builder.Default
    private final List<YearWorkload> years = new ArrayList<>();
}
