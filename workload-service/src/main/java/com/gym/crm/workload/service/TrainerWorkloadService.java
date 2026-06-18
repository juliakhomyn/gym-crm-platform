package com.gym.crm.workload.service;

import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.dto.validation.Month;
import com.gym.crm.workload.dto.validation.Username;
import com.gym.crm.workload.dto.validation.Year;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TrainerWorkloadService {

    Integer getWorkingHours(@Username String username, @Year int year, @Month int month);

    void update(@Valid TrainerWorkloadUpdateDTO workloadUpdateDTO);
}
