package com.gym.crm.workload.model;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class MonthWorkload {
    private final int month;
    private final int trainingDuration;
}
