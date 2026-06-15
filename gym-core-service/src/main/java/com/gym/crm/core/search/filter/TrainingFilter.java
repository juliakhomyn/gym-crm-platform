package com.gym.crm.core.search.filter;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
@ToString
public abstract class TrainingFilter {
    private String username;
    private String joinFullName;
    private LocalDate fromDate;
    private LocalDate toDate;
}
