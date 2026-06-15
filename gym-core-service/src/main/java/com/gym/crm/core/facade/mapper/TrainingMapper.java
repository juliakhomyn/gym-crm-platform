package com.gym.crm.core.facade.mapper;

import com.gym.crm.core.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    Training toEntity(TrainingRequestDTO trainingRequestDTO);

    @Mapping(target = "traineeUsername", source = "trainee.user.username")
    @Mapping(target = "trainerUsername", source = "trainer.user.username")
    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingTypeName", source = "trainingType.trainingTypeName")
    TrainingResponseDTO toDto(Training training);

    TrainingTypeDTO toDto(TrainingType trainingType);
}
