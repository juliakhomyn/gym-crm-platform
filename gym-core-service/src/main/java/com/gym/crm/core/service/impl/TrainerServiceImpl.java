package com.gym.crm.core.service.impl;

import com.gym.crm.core.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerRequestDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerResponseDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.ValidationFailedException;
import com.gym.crm.core.facade.mapper.TrainerMapper;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.TrainingType;
import com.gym.crm.core.model.User;
import com.gym.crm.core.repository.TraineeRepository;
import com.gym.crm.core.repository.TrainerRepository;
import com.gym.crm.core.repository.TrainingTypeRepository;
import com.gym.crm.core.service.TrainerService;
import com.gym.crm.core.service.common.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINING_TYPE_NOT_FOUND_BY_NAME = "Training type not found by name: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";

    private final TrainerRepository repository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserProfileService userProfileService;
    private final TrainerMapper mapper;

    @Transactional
    @Override
    public TrainerResponseDTO createTrainer(TrainerRequestDTO request) {
        log.info("Creating trainer: firstName={} lastName={}", request.getFirstName(), request.getLastName());

        Trainer trainer = mapper.toEntity(request);
        String username = userProfileService.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = userProfileService.generatePassword();

        traineeRepository.findByUserUsername(username).ifPresent(trainee -> {
            log.info("Registration failed: user with username {} is already registered as trainee", username);
            throw new ValidationFailedException(String.format("User with username %s is already registered as a trainee", username));
        });

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(request.getSpecialization()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, request.getSpecialization())));

        User user = trainer.getUser().toBuilder()
                .username(username)
                .password(userProfileService.encodePassword(rawPassword))
                .isActive(true)
                .build();
        Trainer withCredentials = trainer.toBuilder()
                .user(user)
                .specialization(trainingType)
                .build();

        Trainer saved = repository.save(withCredentials);
        log.info("Trainer created successfully: username={}", saved.getUser().getUsername());

        TrainerResponseDTO response = mapper.toDto(saved);

        return response.toBuilder().password(rawPassword).build();
    }

    @Transactional
    @Override
    public TrainerResponseDTO updateTrainer(TrainerUpdateDTO request) {
        log.info("Updating trainer: username={}", request.getUsername());

        Trainer existing = repository.findByUserUsername(request.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, request.getUsername())));
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(request.getSpecialization()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, request.getSpecialization())));

        User user = existing.getUser().toBuilder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.getIsActive())
                .build();
        Trainer updated = existing.toBuilder()
                .user(user)
                .specialization(trainingType)
                .build();

        Trainer saved = repository.save(updated);
        log.info("Trainer updated successfully: username={}", saved.getUser().getUsername());

        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerInfoDTO getTrainerById(Long id) {
        log.info("Getting trainer by id: id={}", id);

        Trainer trainer = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_ID, id)));

        return mapper.toInfoDto(trainer);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerInfoDTO getTrainerByUsername(String username) {
        log.info("Getting trainer by username: username={}", username);

        Trainer trainer = repository.findByUsernameWithTrainees(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, username)));

        return mapper.toInfoDto(trainer);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainerInfoDTO> getAllTrainers() {
        log.info("Getting all trainers");

        return repository.findAll()
                .stream()
                .map(mapper::toInfoDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainerInfoDTO> getNotAssignedToTrainee(String traineeUsername) {
        log.info("Getting all trainers not assigned to trainee: username={}", traineeUsername);

        traineeRepository.findByUserUsername(traineeUsername).orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, traineeUsername)));

        return repository.findNotAssignedToTrainee(traineeUsername).stream()
                .map(mapper::toInfoDto)
                .toList();
    }
}
