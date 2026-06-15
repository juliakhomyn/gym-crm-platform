package com.gym.crm.core.facade.mapper.rest;

import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.core.facade.dto.trainee.TraineeInfoDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeRequestDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeResponseDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeRestMapper {

    TraineeRequestDTO toDto(TraineeCreateRequest request);

    TraineeCreateResponse toRest(TraineeResponseDTO dto);

    @Mapping(target = "trainers", source = "assignedTrainers")
    TraineeGetResponse toRest(TraineeInfoDTO dto);

    @Mapping(target = "username", source = "username")
    TraineeUpdateDTO toDto(String username, TraineeUpdateRequest request);

    @Mapping(target = "trainers", source = "assignedTrainers")
    TraineeUpdateResponse toRestUpdateResponse(TraineeResponseDTO dto);
}
