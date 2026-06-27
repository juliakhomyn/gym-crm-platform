package com.gym.crm.core.service.impl;

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
import com.gym.crm.core.messaging.ActionType;
import com.gym.crm.core.messaging.TrainerWorkloadMapper;
import com.gym.crm.core.messaging.TrainerWorkloadMessageSender;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.User;
import com.gym.crm.core.repository.TraineeRepository;
import com.gym.crm.core.repository.TrainerRepository;
import com.gym.crm.core.service.TraineeService;
import com.gym.crm.core.service.common.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";

    private final TraineeRepository repository;
    private final TrainerRepository trainerRepository;
    private final UserProfileService userProfileService;
    private final TraineeMapper mapper;
    private final TrainerMapper trainerMapper;
    private final TrainerWorkloadMapper workloadMapper;
    private final TrainerWorkloadMessageSender workloadMessageSender;

    @Transactional
    @Override
    public TraineeResponseDTO createTrainee(TraineeRequestDTO request) {
        log.info("Creating trainee: firstName={}, lastName={}", request.getFirstName(), request.getLastName());

        Trainee trainee = mapper.toEntity(request);
        String username = userProfileService.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = userProfileService.generatePassword();

        trainerRepository.findByUserUsername(username).ifPresent(trainer -> {
            log.info("Registration failed: user with username {} is already registered as trainer", username);
            throw new ValidationFailedException(String.format("User with username %s is already registered as a trainer", username));
        });

        User user = trainee.getUser().toBuilder()
                .username(username)
                .password(userProfileService.encodePassword(rawPassword))
                .isActive(true)
                .build();
        Trainee withCredentials = trainee.toBuilder()
                .user(user)
                .build();

        Trainee saved = repository.save(withCredentials);
        log.info("Trainee created successfully: username={}", saved.getUser().getUsername());

        TraineeResponseDTO response = mapper.toDto(saved);

        return response.toBuilder().password(rawPassword).build();
    }

    @Transactional
    @Override
    public TraineeResponseDTO updateTrainee(TraineeUpdateDTO request) {
        log.info("Updating trainee: username={}", request.getUsername());

        Trainee existing = repository.findByUserUsername(request.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, request.getUsername())));

        User user = existing.getUser().toBuilder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.getIsActive())
                .build();
        Trainee updated = existing.toBuilder()
                .user(user)
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();

        Trainee saved = repository.save(updated);
        log.info("Trainee updated successfully: username={}", saved.getUser().getUsername());

        return mapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteByUsername(String username) {
        log.info("Deleting trainee by username: username={}", username);
        Trainee trainee = repository.findByUsernameWithTrainers(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, username)));

        trainee.getTrainers().clear();
        repository.save(trainee);
        repository.delete(trainee);
        log.info("Trainee deleted successfully: username={}", username);

        trainee.getTrainings().stream()
                .map(training -> workloadMapper.toMessage(training, ActionType.DELETE))
                .forEach(workloadMessageSender::sendUpdate);
    }

    @Transactional(readOnly = true)
    @Override
    public TraineeInfoDTO getTraineeById(Long id) {
        log.info("Getting trainee by id: id={}", id);

        Trainee trainee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_ID, id)));

        return mapper.toInfoDto(trainee);
    }

    @Transactional(readOnly = true)
    @Override
    public TraineeInfoDTO getTraineeByUsername(String username) {
        log.info("Getting trainee by username: username={}", username);

        Trainee trainee = repository.findByUsernameWithTrainers(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, username)));

        return mapper.toInfoDto(trainee);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TraineeInfoDTO> getAllTrainees() {
        log.info("Getting all trainees");

        return repository.findAll()
                .stream()
                .map(mapper::toInfoDto)
                .toList();
    }

    @Transactional
    @Override
    public List<TrainerInfoDTO> updateTrainersList(TrainerAssignmentUpdateDTO dto) {
        log.info("Updating trainers list for trainee: username={}, trainers' usernames={}", dto.getTraineeUsername(), dto.getTrainerUsernames());

        Trainee trainee = repository.findByUsernameWithTrainers(dto.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, dto.getTraineeUsername())));

        List<Trainer> trainers = dto.getTrainerUsernames().stream()
                .map(username -> trainerRepository.findByUserUsername(username)
                        .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, username))))
                .toList();

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(trainers);
        repository.save(trainee);

        log.info("Trainers list updated successfully: username={}", dto.getTraineeUsername());

        return trainee.getTrainers().stream()
                .map(trainerMapper::toInfoDtoWithoutTrainees)
                .toList();
    }
}
