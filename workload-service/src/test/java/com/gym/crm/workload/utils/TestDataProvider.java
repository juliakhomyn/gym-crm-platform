package com.gym.crm.workload.utils;

import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.model.MonthWorkload;
import com.gym.crm.workload.model.TrainerWorkload;
import com.gym.crm.workload.model.YearWorkload;
import com.gym.crm.workload.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workload.openapi.model.TrainerWorkloadResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.gym.crm.workload.openapi.model.ActionType.ADD;

public class TestDataProvider {
    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";
    private static final int DURATION = 60;
    private static final int YEAR = 2026;
    private static final int MONTH = 1;

    public static TrainerWorkloadUpdateDTO buildTrainerWorkloadUpdateDTO(ActionType actionType) {
        return TrainerWorkloadUpdateDTO.builder()
                .trainerUsername(USERNAME)
                .trainerFirstName(FIRST_NAME)
                .trainerLastName(LAST_NAME)
                .isActive(true)
                .trainingDate(LocalDate.of(YEAR, 1, 1))
                .trainingDuration(DURATION)
                .actionType(actionType)
                .build();
    }

    public static TrainerWorkload buildTrainerWorkload() {
        List<YearWorkload> yearWorkloads = new ArrayList<>(List.of(buildYearWorkload()));

        return TrainerWorkload.builder()
                .trainerUsername(USERNAME)
                .trainerFirstName(FIRST_NAME)
                .trainerLastName(LAST_NAME)
                .isActive(true)
                .years(yearWorkloads)
                .build();
    }

    public static YearWorkload buildYearWorkload() {
        List<MonthWorkload> monthWorkloads = new ArrayList<>(List.of(buildMonthWorkload()));

        return YearWorkload.builder()
                .year(YEAR)
                .months(monthWorkloads)
                .build();
    }

    public static MonthWorkload buildMonthWorkload() {
        return MonthWorkload.builder()
                .month(1)
                .trainingDuration(DURATION)
                .build();
    }

    public static TrainerWorkloadRequest buildTrainerWorkloadRequest() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(USERNAME);
        request.setTrainerFirstName(FIRST_NAME);
        request.setTrainerLastName(LAST_NAME);
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.of(YEAR, MONTH, 1));
        request.setTrainingDuration(DURATION);
        request.setActionType(ADD);

        return request;
    }
}
