package com.gym.crm.core.search.filter;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
public class TraineeTrainingFilter extends TrainingFilter {
    private String trainingTypeName;
}
