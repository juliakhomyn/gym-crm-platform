package com.gym.crm.core.facade;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gia.openapi.model.LoginResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.core.facade.dto.common.AuthRequestDTO;
import com.gym.crm.core.facade.dto.common.AuthResponseDTO;
import com.gym.crm.core.facade.dto.common.PasswordChangeRequest;
import com.gym.crm.core.facade.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeInfoDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeRequestDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeResponseDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.core.facade.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerRequestDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerResponseDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.core.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.core.facade.mapper.rest.TraineeRestMapper;
import com.gym.crm.core.facade.mapper.rest.TrainerRestMapper;
import com.gym.crm.core.facade.mapper.rest.TrainingRestMapper;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import com.gym.crm.core.service.TraineeService;
import com.gym.crm.core.service.TrainerService;
import com.gym.crm.core.service.TrainingService;
import com.gym.crm.core.service.UserService;
import com.gym.crm.core.service.common.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    private final TraineeRestMapper traineeRestMapper;
    private final TrainerRestMapper trainerRestMapper;
    private final TrainingRestMapper trainingRestMapper;

    public LoginResponse login(LoginRequest request) {
        AuthRequestDTO dto = AuthRequestDTO.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        AuthResponseDTO responseDTO = authenticationService.authenticate(dto);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUsername(responseDTO.getUsername());
        loginResponse.setToken(responseDTO.getToken());

        return loginResponse;
    }

    public void logout(HttpServletRequest request) {
        authenticationService.logout(request);
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest request) {
        TraineeRequestDTO dto = traineeRestMapper.toDto(request);
        TraineeResponseDTO traineeResponseDTO = traineeService.createTrainee(dto);

        return traineeRestMapper.toRest(traineeResponseDTO);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public TraineeUpdateResponse updateTrainee(TraineeUpdateRequest request, String username) {
        TraineeUpdateDTO dto = traineeRestMapper.toDto(username, request);
        TraineeResponseDTO traineeResponseDTO = traineeService.updateTrainee(dto);

        return traineeRestMapper.toRestUpdateResponse(traineeResponseDTO);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    public TraineeGetResponse getTraineeByUsername(String username) {
        TraineeInfoDTO traineeInfoDTO = traineeService.getTraineeByUsername(username);

        return traineeRestMapper.toRest(traineeInfoDTO);
    }

    public List<TraineeGetResponse> getAllTrainees() {
        return traineeService.getAllTrainees().stream()
                .map(traineeRestMapper::toRest)
                .toList();
    }

    @PreAuthorize("#username == authentication.principal.username")
    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainersList(TraineeAssignedTrainersUpdateRequest request, String username) {
        TrainerAssignmentUpdateDTO dto = TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(username)
                .trainerUsernames(request.getTrainerUsernames())
                .build();
        List<TrainerInfoDTO> list = traineeService.updateTrainersList(dto);
        List<AssignedTrainerResponse> assignedTrainers = list.stream()
                .map(trainerRestMapper::toRest)
                .toList();

        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(assignedTrainers);

        return response;
    }

    public TrainerCreateResponse createTrainer(TrainerCreateRequest request) {
        TrainerRequestDTO dto = trainerRestMapper.toDto(request);
        TrainerResponseDTO trainerResponseDTO = trainerService.createTrainer(dto);

        return trainerRestMapper.toRest(trainerResponseDTO);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public TrainerUpdateResponse updateTrainer(TrainerUpdateRequest request, String username) {
        TrainerUpdateDTO dto = trainerRestMapper.toDto(username, request);
        TrainerResponseDTO trainerResponseDTO = trainerService.updateTrainer(dto);

        return trainerRestMapper.toRestUpdateResponse(trainerResponseDTO);
    }

    public TrainerGetResponse getTrainerByUsername(String username) {
        TrainerInfoDTO trainerInfoDTO = trainerService.getTrainerByUsername(username);

        return trainerRestMapper.toRestGetResponse(trainerInfoDTO);
    }

    public List<TrainerGetResponse> getAllTrainers() {
        return trainerService.getAllTrainers().stream()
                .map(trainerRestMapper::toRestGetResponse)
                .toList();
    }

    @PreAuthorize("#username == authentication.principal.username")
    public List<AssignedTrainerResponse> getTrainersNotAssignedToTrainee(String username) {
        List<TrainerInfoDTO> trainers = trainerService.getNotAssignedToTrainee(username);

        return trainers.stream()
                .map(trainerRestMapper::toRest)
                .toList();
    }

    @PreAuthorize("#request.username == authentication.principal.username")
    public void changePassword(LoginChangeRequest request) {
        PasswordChangeRequest requestDTO = PasswordChangeRequest.builder()
                .username(request.getUsername())
                .oldPassword(request.getOldPassword())
                .newPassword(request.getNewPassword())
                .build();

        userService.changePassword(requestDTO);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public void toggleActiveStatus(ActivationStatusRequest request, String username) {
        ToggleActiveRequestDTO dto = ToggleActiveRequestDTO.builder()
                .username(username)
                .isActive(request.getIsActive())
                .build();

        userService.toggleActive(dto);
    }

    public void createTraining(TrainingCreateRequest request) {
        TrainingRequestDTO dto = trainingRestMapper.toDto(request);

        trainingService.createTraining(dto);
    }

    @PreAuthorize("#trainerUsername == authentication.principal.username")
    public void deleteTraining(Long id, String trainerUsername) {
        trainingService.deleteById(id, trainerUsername);
    }

    @PreAuthorize("#filter.username == authentication.principal.username")
    public List<GetTraineeTrainingResponse> getTraineeTrainingsByFilter(TraineeTrainingFilter filter) {
        return trainingService.getTraineeTrainings(filter).stream()
                .map(trainingRestMapper::toRestTraineeResponse)
                .toList();
    }

    @PreAuthorize("#filter.username == authentication.principal.username")
    public List<GetTrainerTrainingResponse> getTrainerTrainingsByFilter(TrainerTrainingFilter filter) {
        return trainingService.getTrainerTrainings(filter).stream()
                .map(trainingRestMapper::toRestTrainerResponse)
                .toList();
    }

    public List<TrainingTypeResponse> getTrainingTypes() {
        return trainingService.getAllTrainingTypes().stream()
                .map(trainingRestMapper::toRest)
                .toList();
    }
}
