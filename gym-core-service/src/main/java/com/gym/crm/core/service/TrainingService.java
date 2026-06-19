package com.gym.crm.core.service;

import com.gym.crm.core.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
import com.gym.crm.core.facade.dto.validation.ValidId;
import com.gym.crm.core.facade.dto.validation.ValidUsername;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface TrainingService {
    TrainingResponseDTO createTraining(@Valid TrainingRequestDTO trainingRequestDTO);

    void deleteById(@ValidId Long id, @ValidUsername String trainerUsername);

    TrainingResponseDTO getTrainingById(@ValidId Long id);

    List<TrainingResponseDTO> getAllTrainings();

    List<TrainingResponseDTO> getTraineeTrainings(@Valid TraineeTrainingFilter filter);

    List<TrainingResponseDTO> getTrainerTrainings(@Valid TrainerTrainingFilter filter);

    List<TrainingTypeDTO> getAllTrainingTypes();
}
