package com.gym.crm.core.service;

import com.gym.crm.core.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerRequestDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerResponseDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.ValidationFailedException;
import com.gym.crm.core.facade.mapper.TrainerMapper;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.TrainingType;
import com.gym.crm.core.repository.TraineeRepository;
import com.gym.crm.core.repository.TrainerRepository;
import com.gym.crm.core.repository.TrainingTypeRepository;
import com.gym.crm.core.service.common.UserProfileService;
import com.gym.crm.core.service.impl.TrainerServiceImpl;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final String FIRST_NAME = "Owen";
    private static final String LAST_NAME = "Castleberry";
    private static final String USERNAME = "Owen.Castleberry";
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final String SPECIALIZATION = "Yoga";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINING_TYPE_NOT_FOUND_BY_NAME = "Training type not found by name: %s";
    private static final String USER_REGISTERED_AS_TRAINEE = "User with username %s is already registered as a trainee";

    @Mock
    private TrainerRepository repository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private TrainerMapper mapper;

    @InjectMocks
    private TrainerServiceImpl service;

    private final Trainer trainer = TestDataProvider.buildTrainer();
    private final Trainer savedTrainer = TestDataProvider.buildSavedTrainer();
    private final TrainerRequestDTO request = TestDataProvider.buildTrainerRequestDTO();
    private final TrainerUpdateDTO updateDTO = TestDataProvider.buildTrainerUpdateDTO();
    private final TrainerResponseDTO response = TestDataProvider.buildTrainerResponseDTO();
    private final TrainerInfoDTO info = TestDataProvider.buildTrainerInfoDTO();
    private final TrainingType trainingType = TestDataProvider.buildTrainingType();

    @Test
    void createTrainer_shouldSaveTrainerWithCredentials() {
        TrainerResponseDTO expected = response.toBuilder().password(RAW_PASSWORD).build();

        when(mapper.toEntity(request)).thenReturn(trainer);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(trainingTypeRepository.findByTrainingTypeName(SPECIALIZATION)).thenReturn(Optional.of(trainingType));
        when(repository.save(any(Trainer.class))).thenReturn(savedTrainer);
        when(mapper.toDto(savedTrainer)).thenReturn(response);

        TrainerResponseDTO actual = service.createTrainer(request);

        assertThat(actual).isEqualTo(expected);
        verify(mapper).toEntity(request);
        verify(userProfileService).generateUsername(FIRST_NAME, LAST_NAME);
        verify(userProfileService).generatePassword();
        verify(userProfileService).encodePassword(RAW_PASSWORD);
        verify(repository).save(any(Trainer.class));
        verify(mapper).toDto(savedTrainer);
    }

    @Test
    void createTrainer_shouldThrowException_whenTrainingTypeNotFound() {
        when(trainingTypeRepository.findByTrainingTypeName(SPECIALIZATION)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTrainer(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, SPECIALIZATION));
        verify(repository, never()).save(any(Trainer.class));
    }

    @Test
    void createTrainee_shouldThrowValidationFailedException_ifTrainerExists() {
        when(mapper.toEntity(request)).thenReturn(trainer);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(new Trainee()));

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.createTrainer(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_REGISTERED_AS_TRAINEE, USERNAME));
        verify(repository, never()).save(any());
    }

    @Test
    void updateTrainer_shouldUpdateTrainer_whenTrainerExists() {
        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.ofNullable(savedTrainer));
        when(trainingTypeRepository.findByTrainingTypeName(SPECIALIZATION)).thenReturn(Optional.of(trainingType));
        when(repository.save(any(Trainer.class))).thenReturn(savedTrainer);
        when(mapper.toDto(savedTrainer)).thenReturn(response);

        TrainerResponseDTO actual = service.updateTrainer(updateDTO);

        assertThat(actual).isEqualTo(response);
        verify(repository).save(any(Trainer.class));
        verify(mapper).toDto(savedTrainer);
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerNotFound() {
        TrainerUpdateDTO nonExistent = TestDataProvider.buildNonExistentTrainerUpdateDTO();
        when(repository.findByUserUsername(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(nonExistent));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(repository, never()).save(any(Trainer.class));
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainingTypeNotFound() {
        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.ofNullable(savedTrainer));
        when(trainingTypeRepository.findByTrainingTypeName(SPECIALIZATION)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(updateDTO));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, SPECIALIZATION));
        verify(repository, never()).save(any(Trainer.class));
    }

    @Test
    void getTrainerById_shouldReturnTrainer_whenTrainerExists() {
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(savedTrainer));
        when(mapper.toInfoDto(savedTrainer)).thenReturn(info);

        TrainerInfoDTO actual = service.getTrainerById(VALID_ID);

        assertThat(actual).isEqualTo(info);
    }

    @Test
    void getTrainerById_shouldThrowException_whenTrainerNotFound() {
        when(repository.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainerById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainer_whenExists() {
        when(repository.findByUsernameWithTrainees(USERNAME)).thenReturn(Optional.of(trainer));
        when(mapper.toInfoDto(trainer)).thenReturn(info);

        TrainerInfoDTO actual = service.getTrainerByUsername(USERNAME);

        assertThat(actual).isEqualTo(info);
        verify(repository).findByUsernameWithTrainees(USERNAME);
    }

    @Test
    void getTrainerByUsername_shouldThrowException_whenNotFound() {
        when(repository.findByUsernameWithTrainees(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainerByUsername(NOT_FOUND_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(repository).findByUsernameWithTrainees(NOT_FOUND_USERNAME);
    }

    @Test
    void getAllTrainers_shouldReturnAllTrainers_whenExist() {
        when(repository.findAll()).thenReturn(List.of(savedTrainer));
        when(mapper.toInfoDto(savedTrainer)).thenReturn(info);

        List<TrainerInfoDTO> actual = service.getAllTrainers();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAllTrainers_shouldReturnEmptyList_whenNoTrainers() {
        when(repository.findAll()).thenReturn(List.of());

        List<TrainerInfoDTO> actual = service.getAllTrainers();

        assertThat(actual).isEmpty();
    }

    @Test
    void getNotAssignedToTrainee_shouldReturnListOfTrainerInfoDTOs() {
        Trainer trainer1 = TestDataProvider.buildTrainer(1L, "trainer1");
        Trainer trainer2 = TestDataProvider.buildTrainer(2L, "trainer2");
        TrainerInfoDTO trainerInfoDTO1 = TestDataProvider.buildNotAssignedTrainerInfoDTO("trainer1");
        TrainerInfoDTO trainerInfoDTO2 = TestDataProvider.buildNotAssignedTrainerInfoDTO("trainer2");

        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(new Trainee()));
        when(repository.findNotAssignedToTrainee(USERNAME)).thenReturn(List.of(trainer1, trainer2));
        when(mapper.toInfoDto(trainer1)).thenReturn(trainerInfoDTO1);
        when(mapper.toInfoDto(trainer2)).thenReturn(trainerInfoDTO2);

        List<TrainerInfoDTO> actual = service.getNotAssignedToTrainee(USERNAME);

        assertThat(actual)
                .hasSize(2)
                .containsExactlyInAnyOrder(trainerInfoDTO1, trainerInfoDTO2);
        verify(repository).findNotAssignedToTrainee(USERNAME);
        verify(mapper).toInfoDto(trainer1);
        verify(mapper).toInfoDto(trainer2);
    }

    @Test
    void getNotAssignedToTrainee_shouldReturnEmptyList_whenNoTrainers() {
        when(repository.findNotAssignedToTrainee(USERNAME)).thenReturn(List.of());
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(new Trainee()));

        List<TrainerInfoDTO> actual = service.getNotAssignedToTrainee(USERNAME);

        assertThat(actual).isEmpty();
        verify(repository).findNotAssignedToTrainee(USERNAME);
        verify(mapper, never()).toInfoDto(any());
    }

    @Test
    void getNotAssignedToTrainee_shouldThrow_whenTraineeNotFound() {
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getNotAssignedToTrainee(USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, USERNAME));
        verify(repository, never()).findNotAssignedToTrainee(any());
        verify(mapper, never()).toInfoDto(any());
    }
}
