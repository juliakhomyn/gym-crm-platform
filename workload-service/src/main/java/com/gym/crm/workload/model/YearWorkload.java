package com.gym.crm.workload.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class YearWorkload {
    private final int year;
    @Builder.Default
    private final List<MonthWorkload> months =  new ArrayList<>();
}
