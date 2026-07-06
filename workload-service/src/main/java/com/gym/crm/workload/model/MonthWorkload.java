package com.gym.crm.workload.model;

import com.gym.crm.workload.dto.validation.Month;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MonthWorkload {

    @Field(name = "month")
    @Month
    private int month;

    @Field(name = "training_duration")
    @NotNull
    @Positive
    private int trainingDuration;
}
