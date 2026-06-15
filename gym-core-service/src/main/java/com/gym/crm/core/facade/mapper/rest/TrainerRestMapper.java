package com.gym.crm.core.facade.mapper.rest;

import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
import com.gym.crm.core.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerRequestDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerResponseDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerRestMapper {

    TrainerRequestDTO toDto(TrainerCreateRequest request);

    TrainerCreateResponse toRest(TrainerResponseDTO dto);

    @Mapping(target = "trainees", source = "assignedTrainees")
    TrainerGetResponse toRestGetResponse(TrainerInfoDTO dto);

    AssignedTrainerResponse toRest(TrainerInfoDTO dto);

    @Mapping(target = "username", source = "username")
    TrainerUpdateDTO toDto(String username, TrainerUpdateRequest request);

    @Mapping(target = "trainees", source = "assignedTrainees")
    TrainerUpdateResponse toRestUpdateResponse(TrainerResponseDTO dto);
}
