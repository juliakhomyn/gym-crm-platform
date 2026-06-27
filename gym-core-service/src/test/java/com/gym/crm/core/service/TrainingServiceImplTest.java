package com.gym.crm.core.service;

import com.gym.crm.core.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.facade.mapper.TrainingMapper;
import com.gym.crm.core.messaging.ActionType;
import com.gym.crm.core.messaging.TrainerWorkloadMapper;
import com.gym.crm.core.messaging.TrainerWorkloadMessage;
import com.gym.crm.core.messaging.TrainerWorkloadMessageSender;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.model.TrainingType;
import com.gym.crm.core.repository.TraineeRepository;
import com.gym.crm.core.repository.TrainerRepository;
import com.gym.crm.core.repository.TrainingRepositoryCriteria;
import com.gym.crm.core.repository.TrainingRepository;
import com.gym.crm.core.repository.TrainingTypeRepository;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import com.gym.crm.core.service.impl.TrainingServiceImpl;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    private static final String TRAINEE_USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINING_TYPE_NOT_FOUND_BY_NAME = "Training type not found by name: %s";
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by id: %s";

    private final Trainee trainee = TestDataProvider.buildTrainee();
    private final Trainer trainer = TestDataProvider.buildTrainer();
    private final TrainingType trainingType = TestDataProvider.buildTrainingType();
    private final Training savedTraining = TestDataProvider.buildSavedTraining();
    private final TrainingRequestDTO request = TestDataProvider.buildTrainingRequestDTO();
    private final TrainingResponseDTO response = TestDataProvider.buildTrainingResponseDTO();
    private final TrainingTypeDTO trainingTypeDTO = TestDataProvider.buildTrainingTypeDTO();

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingRepositoryCriteria trainingRepositoryCriteria;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingMapper mapper;
    @Mock
    private TrainerWorkloadMapper workloadMapper;
    @Mock
    private TrainerWorkloadMessageSender workloadMessageSender;

    @InjectMocks
    private TrainingServiceImpl service;

    @Test
    void createTraining_shouldSaveTrainingWithCredentials() {
        TrainerWorkloadMessage workloadMessage = TestDataProvider.buildTrainerWorkloadMessage(ActionType.ADD);
        when(mapper.toEntity(request)).thenReturn(savedTraining);
        when(traineeRepository.findByUserUsername(TRAINEE_USERNAME)).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsername(TRAINER_USERNAME)).thenReturn(Optional.ofNullable(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TRAINING_NAME)).thenReturn(Optional.ofNullable(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);
        when(mapper.toDto(savedTraining)).thenReturn(response);
        when(workloadMapper.toMessage(any(Training.class), any(ActionType.class))).thenReturn(workloadMessage);

        TrainingResponseDTO actual = service.createTraining(request);

        assertThat(actual).isEqualTo(response);
        verify(mapper).toEntity(request);
        verify(mapper).toDto(savedTraining);
        verify(trainingRepository).save(any(Training.class));
        verify(workloadMessageSender).sendUpdate(any(TrainerWorkloadMessage.class));
    }

    @Test
    void deleteById_shouldDeleteAndNotifyWorkload_whenAuthorized() {
        when(trainingRepository.findById(VALID_ID)).thenReturn(Optional.of(savedTraining));
        when(workloadMapper.toMessage(savedTraining, ActionType.DELETE)).thenReturn(mock(TrainerWorkloadMessage.class));

        service.deleteById(VALID_ID, TRAINER_USERNAME);

        verify(trainingRepository).delete(savedTraining);
        verify(workloadMapper).toMessage(savedTraining, ActionType.DELETE);
        verify(workloadMessageSender).sendUpdate(any(TrainerWorkloadMessage.class));
    }

    @Test
    void deleteById_shouldThrowAccessDenied_whenNotAuthorized() {
        when(trainingRepository.findById(VALID_ID)).thenReturn(Optional.of(savedTraining));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.deleteById(VALID_ID, TRAINEE_USERNAME));

        assertThat(exception.getMessage()).contains("Access Denied");
        verify(trainingRepository, never()).delete(any(Training.class));
        verify(workloadMessageSender, never()).sendUpdate(any());
    }

    @Test
    void deleteById_shouldThrowEntityNotFound_whenTrainingNotFound() {
        when(trainingRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.deleteById(VALID_ID, TRAINER_USERNAME));

        assertThat(exception.getMessage()).contains("Training not found");
        verify(trainingRepository, never()).delete(any(Training.class));
        verify(workloadMessageSender, never()).sendUpdate(any());
    }

    @Test
    void updateTrainer_shouldThrowException_whenTraineeNotFound() {
        when(mapper.toEntity(request)).thenReturn(savedTraining);
        when(traineeRepository.findByUserUsername(TRAINEE_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, TRAINEE_USERNAME));
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerNotFound() {
        when(mapper.toEntity(request)).thenReturn(savedTraining);
        when(traineeRepository.findByUserUsername(TRAINEE_USERNAME)).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsername(TRAINER_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_USERNAME, TRAINER_USERNAME));
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainingTypeNotFound() {
        when(mapper.toEntity(request)).thenReturn(savedTraining);
        when(traineeRepository.findByUserUsername(TRAINEE_USERNAME)).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsername(TRAINER_USERNAME)).thenReturn(Optional.ofNullable(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TRAINING_NAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, TRAINING_NAME));
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void getTrainingById_shouldReturnTraining_whenTrainingExists() {
        when(trainingRepository.findById(VALID_ID)).thenReturn(Optional.of(savedTraining));
        when(mapper.toDto(savedTraining)).thenReturn(response);

        TrainingResponseDTO actual = service.getTrainingById(VALID_ID);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void getTrainingById_shouldThrowException_whenTrainingNotFound() {
        when(trainingRepository.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainingById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINING_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getAllTrainings_shouldReturnAllTrainings_whenExist() {
        when(trainingRepository.findAll()).thenReturn(List.of(savedTraining));
        when(mapper.toDto(savedTraining)).thenReturn(response);

        List<TrainingResponseDTO> actual = service.getAllTrainings();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAllTrainings_shouldReturnEmptyList_whenNoTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of());

        List<TrainingResponseDTO> actual = service.getAllTrainings();

        assertThat(actual).isEmpty();
    }

    @Test
    void getTraineeTrainings_shouldReturnList_whenValidFilter() {
        TraineeTrainingFilter filter = TestDataProvider.buildTraineeTrainingFilter();
        List<Training> trainings = List.of(savedTraining);
        TrainingResponseDTO expected = TestDataProvider.buildTrainingResponseDTO();

        when(trainingRepositoryCriteria.findByTraineeCriteria(filter)).thenReturn(trainings);
        when(mapper.toDto(savedTraining)).thenReturn(expected);

        List<TrainingResponseDTO> actual = service.getTraineeTrainings(filter);

        assertThat(actual)
                .hasSize(1)
                .contains(expected);
        verify(trainingRepositoryCriteria).findByTraineeCriteria(filter);
        verify(mapper).toDto(savedTraining);
    }

    @Test
    void getTrainerTrainings_shouldReturnList_whenValidFilter() {
        TrainerTrainingFilter filter = TestDataProvider.buildTrainerTrainingFilter();
        List<Training> trainings = List.of(savedTraining);
        TrainingResponseDTO expected = TestDataProvider.buildTrainingResponseDTO();

        when(trainingRepositoryCriteria.findByTrainerCriteria(filter)).thenReturn(trainings);
        when(mapper.toDto(savedTraining)).thenReturn(expected);

        List<TrainingResponseDTO> actual = service.getTrainerTrainings(filter);

        assertThat(actual)
                .hasSize(1)
                .contains(expected);
        verify(trainingRepositoryCriteria).findByTrainerCriteria(filter);
        verify(mapper).toDto(savedTraining);
    }

    @Test
    void getAllTrainingTypes_shouldReturnAllTrainingTypes_whenExist() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of(trainingType));
        when(mapper.toDto(trainingType)).thenReturn(trainingTypeDTO);

        List<TrainingTypeDTO> actual = service.getAllTrainingTypes();

        assertThat(actual).hasSize(1);
    }
}
