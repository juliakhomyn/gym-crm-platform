package com.gym.crm.workload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class YearWorkload {
    private final int year;
    private final List<MonthWorkload> months;
}
