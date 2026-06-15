package com.gym.crm.core.facade.mapper.rest;

import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.core.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingRestMapper {

    TrainingRequestDTO toDto(TrainingCreateRequest request);

    @Mapping(target = "name", source = "trainingTypeName")
    TrainingTypeResponse toRest(TrainingTypeDTO dto);

    @Mapping(target = "trainingType", source = "trainingTypeName")
    @Mapping(target = "trainerName", source = "trainerUsername")
    GetTraineeTrainingResponse toRestTraineeResponse(TrainingResponseDTO dto);

    @Mapping(target = "trainingType", source = "trainingTypeName")
    @Mapping(target = "traineeName", source = "traineeUsername")
    GetTrainerTrainingResponse toRestTrainerResponse(TrainingResponseDTO dto);
}
