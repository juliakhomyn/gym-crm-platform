package com.gym.crm.workload.mapper;

import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.openapi.model.TrainerWorkloadRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainerWorkloadMapper {

    TrainerWorkloadUpdateDTO toUpdateDTO(TrainerWorkloadRequest trainerWorkloadRequest);
}
