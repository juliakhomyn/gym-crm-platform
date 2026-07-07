package com.gym.crm.workload.model;

import com.gym.crm.workload.dto.validation.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class YearWorkload {

    @Field(name = "year")
    @Year
    private int year;

    @Field(name = "months")
    @Builder.Default
    private List<MonthWorkload> months =  new ArrayList<>();
}
