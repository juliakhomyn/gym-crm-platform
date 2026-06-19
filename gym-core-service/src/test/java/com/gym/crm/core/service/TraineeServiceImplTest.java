package com.gym.crm.core.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.core.client.TrainerWorkloadClientService;
import com.gym.crm.core.facade.dto.trainee.TraineeInfoDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeRequestDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeResponseDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.core.facade.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.ValidationFailedException;
import com.gym.crm.core.facade.mapper.TraineeMapper;
import com.gym.crm.core.facade.mapper.TrainerMapper;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.repository.TraineeRepository;
import com.gym.crm.core.repository.TrainerRepository;
import com.gym.crm.core.service.common.UserProfileService;
import com.gym.crm.core.service.impl.TraineeServiceImpl;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private static final String FIRST_NAME = "Simone";
    private static final String LAST_NAME = "Radcliffe";
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME1 = "trainer1";
    private static final String TRAINER_USERNAME2 = "trainer2";
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final long VALID_ID = 1L;
    private static final long VALID_ID1 = 2L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String USER_REGISTERED_AS_TRAINER = "User with username %s is already registered as a trainer";

    private final Trainee trainee = TestDataProvider.buildTrainee();
    private final Trainee savedTrainee = TestDataProvider.buildSavedTrainee();
    private final TraineeRequestDTO request = TestDataProvider.buildTraineeRequestDTO();
    private final TraineeResponseDTO response = TestDataProvider.buildTraineeResponseDTO();
    private final TraineeInfoDTO info = TestDataProvider.buildTraineeInfoDTO();
    private final TrainerAssignmentUpdateDTO trainerAssignmentUpdateDTO = TestDataProvider.buildValidTrainerAssignmentUpdateDto();

    @Mock
    private TraineeRepository repository;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private TraineeMapper mapper;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainerWorkloadClientService workloadClientService;

    @InjectMocks
    private TraineeServiceImpl service;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        logger.detachAppender(logAppender);
    }

    @Test
    void createTrainee_shouldSaveTraineeWithCredentials() {
        TraineeResponseDTO expected = response.toBuilder().password(RAW_PASSWORD).build();

        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(repository.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        TraineeResponseDTO actual = service.createTrainee(request);

        assertThat(actual).isEqualTo(expected);
        verify(mapper).toEntity(request);
        verify(userProfileService).generateUsername(FIRST_NAME, LAST_NAME);
        verify(userProfileService).generatePassword();
        verify(userProfileService).encodePassword(RAW_PASSWORD);
        verify(repository).save(any(Trainee.class));
        verify(mapper).toDto(savedTrainee);
    }

    @Test
    void createTrainee_shouldThrowValidationFailedException_ifTrainerExists() {
        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(trainerRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(new Trainer()));

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.createTrainee(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_REGISTERED_AS_TRAINER, USERNAME));
        verify(repository, never()).save(any());
    }

    @Test
    void updateTrainee_shouldUpdateTrainee_whenTraineeExists() {
        TraineeUpdateDTO updateDTO = TestDataProvider.buildTraineeUpdateDTO();

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.ofNullable(savedTrainee));
        when(repository.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        TraineeResponseDTO actual = service.updateTrainee(updateDTO);

        assertThat(actual).isEqualTo(response);
        verify(repository).save(any(Trainee.class));
        verify(mapper).toDto(savedTrainee);
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeNotFound() {
        TraineeUpdateDTO nonExistent = TestDataProvider.buildNonExistentTraineeUpdateDTO();
        when(repository.findByUserUsername(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainee(nonExistent));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(repository, never()).save(any(Trainee.class));
    }

    @Test
    void deleteByUsername_shouldDeleteAndNotifyWorkload_whenTraineeExists() {
        Trainee traineeWithTrainers = TestDataProvider.buildTraineeWithTrainers(new HashSet<>());
        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.of(traineeWithTrainers));

        service.deleteByUsername(USERNAME);

        verify(repository).save(traineeWithTrainers);
        verify(repository).delete(traineeWithTrainers);
        verify(workloadClientService, times(1)).notifyTrainingDeleted(any(Training.class));
    }

    @Test
    void deleteByUsername_shouldThrow_whenTraineeNotFound() {
        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.deleteByUsername(USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, USERNAME));
        verify(repository, never()).save(any());
        verify(repository, never()).delete(any(Trainee.class));
    }

    @Test
    void deleteByUsername_shouldDeleteAndNotNotifyWorkload_whenTraineeHasNoTrainings() {
        Trainee traineeWithTrainers = TestDataProvider.buildTraineeWithTrainers(new HashSet<>()).toBuilder()
                .trainings(Set.of())
                .build();

        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.of(traineeWithTrainers));

        service.deleteByUsername(USERNAME);

        verify(repository).save(traineeWithTrainers);
        verify(repository).delete(traineeWithTrainers);
        verify(workloadClientService, never()).notifyTrainingDeleted(any());
    }

    @Test
    void getTraineeById_shouldReturnTrainee_whenTraineeExists() {
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(savedTrainee));
        when(mapper.toInfoDto(savedTrainee)).thenReturn(info);

        TraineeInfoDTO actual = service.getTraineeById(VALID_ID);

        assertThat(actual).isEqualTo(info);
    }

    @Test
    void gerTraineeById_shouldThrowException_whenTraineeNotFound() {
        when(repository.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraineeById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getTraineeByUsername_shouldReturnTrainee_whenExists() {
        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.of(trainee));
        when(mapper.toInfoDto(trainee)).thenReturn(info);

        TraineeInfoDTO actual = service.getTraineeByUsername(USERNAME);

        assertThat(actual).isEqualTo(info);
        verify(repository).findByUsernameWithTrainers(USERNAME);
    }

    @Test
    void getTraineeByUsername_shouldThrowException_whenNotFound() {
        when(repository.findByUsernameWithTrainers(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraineeByUsername(NOT_FOUND_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(repository).findByUsernameWithTrainers(NOT_FOUND_USERNAME);
    }

    @Test
    void getAllTrainees_shouldReturnAllTrainees_whenExist() {
        when(repository.findAll()).thenReturn(List.of(savedTrainee));
        when(mapper.toInfoDto(savedTrainee)).thenReturn(info);

        List<TraineeInfoDTO> actual = service.getAllTrainees();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList_whenNoTrainees() {
        when(repository.findAll()).thenReturn(List.of());

        List<TraineeInfoDTO> actual = service.getAllTrainees();

        assertThat(actual).isEmpty();
    }

    @Test
    void createTrainee_shouldLogInfo_whenCreatingTrainee() {
        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(repository.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        service.createTrainee(request);

        assertThat(logAppender.list)
                .filteredOn(log -> log.getLevel() == Level.INFO)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains(FIRST_NAME) && message.contains(LAST_NAME))
                .anyMatch(message -> message.contains(USERNAME));
    }

    @Test
    void updateTrainersList_shouldUpdateTrainers_whenAllExist() {
        Trainer trainer1 = TestDataProvider.buildTrainer(VALID_ID, TRAINER_USERNAME1);
        Trainer trainer2 = TestDataProvider.buildTrainer(VALID_ID1, TRAINER_USERNAME2);
        Trainee traineeWithTrainers = TestDataProvider.buildTraineeWithTrainers(new HashSet<>(Set.of(trainer1, trainer2)));
        TrainerInfoDTO trainerInfo1 = TestDataProvider.buildTrainerInfoDTO(TRAINER_USERNAME1);
        TrainerInfoDTO trainerInfo2 = TestDataProvider.buildTrainerInfoDTO(TRAINER_USERNAME2);

        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.of(traineeWithTrainers));
        when(trainerRepository.findByUserUsername(TRAINER_USERNAME1)).thenReturn(Optional.of(trainer1));
        when(trainerRepository.findByUserUsername(TRAINER_USERNAME2)).thenReturn(Optional.of(trainer2));
        when(repository.save(traineeWithTrainers)).thenReturn(traineeWithTrainers);
        when(trainerMapper.toInfoDtoWithoutTrainees(trainer1)).thenReturn(trainerInfo1);
        when(trainerMapper.toInfoDtoWithoutTrainees(trainer2)).thenReturn(trainerInfo2);

        List<TrainerInfoDTO> actual = service.updateTrainersList(trainerAssignmentUpdateDTO);

        verify(repository).findByUsernameWithTrainers(USERNAME);
        verify(trainerRepository).findByUserUsername(TRAINER_USERNAME1);
        verify(trainerRepository).findByUserUsername(TRAINER_USERNAME2);
        verify(repository).save(traineeWithTrainers);
        verify(trainerMapper).toInfoDtoWithoutTrainees(trainer1);
        verify(trainerMapper).toInfoDtoWithoutTrainees(trainer2);
        assertThat(actual).containsExactlyInAnyOrder(trainerInfo1, trainerInfo2);
    }

    @Test
    void updateTrainersList_shouldThrow_whenTraineeNotFound() {
        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.updateTrainersList(trainerAssignmentUpdateDTO));

        assertThat(exception.getMessage()).contains(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, USERNAME));
        verify(repository).findByUsernameWithTrainers(USERNAME);
        verify(trainerRepository, never()).findByUserUsername(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void updateTrainersList_shouldThrow_whenTrainerNotFound() {
        when(repository.findByUsernameWithTrainers(USERNAME)).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsername(TRAINER_USERNAME1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainersList(trainerAssignmentUpdateDTO));

        assertThat(exception.getMessage()).contains(String.format(TRAINER_NOT_FOUND_BY_USERNAME, TRAINER_USERNAME1));
        verify(trainerRepository).findByUserUsername(TRAINER_USERNAME1);
        verify(repository, never()).save(any());
    }
}
