package com.gym.crm.core.service;

import com.gym.crm.core.facade.dto.trainee.TraineeInfoDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeRequestDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeResponseDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.core.facade.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.core.facade.dto.validation.ValidId;
import com.gym.crm.core.facade.dto.validation.ValidUsername;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface TraineeService {
    TraineeResponseDTO createTrainee(@Valid TraineeRequestDTO request);

    TraineeResponseDTO updateTrainee(@Valid TraineeUpdateDTO request);

    void deleteByUsername(@ValidUsername String username);

    TraineeInfoDTO getTraineeById(@ValidId Long id);

    TraineeInfoDTO getTraineeByUsername(@ValidUsername String username);

    List<TraineeInfoDTO> getAllTrainees();

    List<TrainerInfoDTO> updateTrainersList(@Valid TrainerAssignmentUpdateDTO dto);
}
