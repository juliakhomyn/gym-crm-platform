package com.gym.crm.workload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthWorkload {
    private final int month;
    private final int trainingDuration;
}
