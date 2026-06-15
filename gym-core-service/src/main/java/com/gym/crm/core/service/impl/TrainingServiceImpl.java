package com.gym.crm.core.service.impl;

import com.gym.crm.core.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.facade.mapper.TrainingMapper;
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
import com.gym.crm.core.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINING_TYPE_NOT_FOUND_BY_NAME = "Training type not found by name: %s";

    private final TrainingRepository trainingRepository;
    private final TrainingRepositoryCriteria trainingRepositoryCriteria;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper mapper;

    @Transactional
    @Override
    public TrainingResponseDTO createTraining(TrainingRequestDTO request) {
        log.info("Creating training: trainingName={}", request.getTrainingName());

        Training mapped = mapper.toEntity(request);

        Trainee trainee = traineeRepository.findByUserUsername(request.getTraineeUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, request.getTraineeUsername())));
        Trainer trainer = trainerRepository.findByUserUsername(request.getTrainerUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, request.getTrainerUsername())));
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(request.getTrainingName()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, request.getTrainingName())));

        Training training = mapped.toBuilder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();

        Training saved = trainingRepository.save(training);
        log.info("Training created successfully: id={}", saved.getId());

        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainingResponseDTO getTrainingById(Long id) {
        log.info("Getting training by id: id={}", id);

        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINING_NOT_FOUND_BY_ID, id)));

        return mapper.toDto(training);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingResponseDTO> getAllTrainings() {
        log.info("Getting all trainings");

        return trainingRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingResponseDTO> getTraineeTrainings(TraineeTrainingFilter filter) {
        log.info("Getting trainee trainings by filter: {}", filter);

        return trainingRepositoryCriteria.findByTraineeCriteria(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingResponseDTO> getTrainerTrainings(TrainerTrainingFilter filter) {
        log.info("Getting trainer trainings by filter: {}", filter);

        return trainingRepositoryCriteria.findByTrainerCriteria(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingTypeDTO> getAllTrainingTypes() {
        return trainingTypeRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }
}
