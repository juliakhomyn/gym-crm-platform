package com.gym.crm.core.utils;

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
import com.gia.openapi.model.AssignedTraineeResponse;
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
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
import com.gym.crm.core.messaging.TrainerWorkloadMessage;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.model.TrainingType;
import com.gym.crm.core.model.User;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestDataProvider {
    private static final String FIRST_NAME = "Simone";
    private static final String LAST_NAME = "Radcliffe";
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String INVALID_PASSWORD = "invalidPassword";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private static final String ADDRESS = "123 Main St";
    private static final String TRAINER_USERNAME1 = "trainer1";
    private static final String TRAINER_USERNAME2 = "trainer2";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final String TRAINER_FIRST_NAME = "Owen";
    private static final String TRAINER_LAST_NAME = "Castleberry";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String SPECIALIZATION = "Yoga";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2024, 1, 15);
    private static final int TRAINING_DURATION = 60;
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final long VALID_ID = 1L;
    private static final LocalDate FROM_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2024, 1, 30);
    private static final String TOKEN = "token";

    public static User buildUser(String firstName, String lastName, String username) {
        return User.builder()
                .id(VALID_ID)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    public static TraineeRequestDTO buildTraineeRequestDTO() {
        return TraineeRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    public static TraineeUpdateDTO buildTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    public static TraineeResponseDTO buildTraineeResponseDTO() {
        return TraineeResponseDTO.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    public static TraineeInfoDTO buildTraineeInfoDTO() {
        return TraineeInfoDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    public static TrainerRequestDTO buildTrainerRequestDTO() {
        return TrainerRequestDTO.builder()
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .specialization(SPECIALIZATION)
                .build();
    }

    public static TrainerUpdateDTO buildTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .username(TRAINER_USERNAME)
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true)
                .build();
    }

    public static TrainerResponseDTO buildTrainerResponseDTO() {
        return TrainerResponseDTO.builder()
                .id(2L)
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .username(TRAINER_USERNAME)
                .isActive(true)
                .build();
    }

    public static TrainerInfoDTO buildTrainerInfoDTO() {
        return TrainerInfoDTO.builder()
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .username(TRAINER_USERNAME)
                .isActive(true)
                .specialization(TRAINING_TYPE_NAME)
                .build();
    }

    public static TrainingRequestDTO buildTrainingRequestDTO() {
        return TrainingRequestDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .build();
    }

    public static TrainingResponseDTO buildTrainingResponseDTO() {
        return TrainingResponseDTO.builder()
                .id(VALID_ID)
                .traineeUsername(USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .build();
    }

    public static ToggleActiveRequestDTO buildToggleActiveRequestDTO() {
        return ToggleActiveRequestDTO.builder()
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    public static TrainerAssignmentUpdateDTO buildTrainerAssignmentUpdateDTO() {
        return TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsernames(List.of(TRAINER_USERNAME))
                .build();
    }

    public static PasswordChangeRequest buildPasswordChangeRequest() {
        return PasswordChangeRequest.builder()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword("newPassword")
                .build();
    }

    public static TraineeTrainingFilter buildTraineeTrainingFilter() {
        return TraineeTrainingFilter.builder()
                .username(USERNAME)
                .fromDate(FROM_DATE)
                .toDate(TO_DATE)
                .joinFullName(TRAINER_FIRST_NAME + " " + TRAINER_LAST_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .build();
    }

    public static TrainerTrainingFilter buildTrainerTrainingFilter() {
        return TrainerTrainingFilter.builder()
                .username(TRAINER_USERNAME)
                .fromDate(FROM_DATE)
                .toDate(TO_DATE)
                .joinFullName(FIRST_NAME + " " + LAST_NAME)
                .build();
    }

    public static AuthRequestDTO buildAuthRequestDTO() {
        return AuthRequestDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    public static AuthResponseDTO buildAuthResponseDTO() {
        return AuthResponseDTO.builder()
                .username(USERNAME)
                .token(TOKEN)
                .build();
    }

    public static LoginRequest buildLoginRequest() {
        return new LoginRequest(USERNAME, PASSWORD);
    }

    public static LoginChangeRequest buildLoginChangeRequest() {
        return new LoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);
    }

    public static TraineeCreateRequest buildTraineeCreateRequest() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setAddress(ADDRESS);

        return request;
    }

    public static TraineeCreateRequest buildTraineeCreateRequestOnlyRequiredFields() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);

        return request;
    }

    public static TraineeCreateResponse buildTraineeCreateResponse() {
        return new TraineeCreateResponse(USERNAME, PASSWORD);
    }

    public static TraineeUpdateRequest buildTraineeUpdateRequest() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setAddress(ADDRESS);
        request.isActive(true);

        return request;
    }

    public static TraineeUpdateResponse buildTraineeUpdateResponse() {
        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setAddress(ADDRESS);
        response.isActive(true);
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    public static AssignedTrainerResponse buildAssignedTrainerResponse() {
        AssignedTrainerResponse response = new AssignedTrainerResponse();
        response.setUsername(TRAINER_USERNAME);
        response.setFirstName(TRAINER_FIRST_NAME);
        response.setLastName(TRAINER_LAST_NAME);
        response.setSpecialization(SPECIALIZATION);

        return response;
    }

    public static ActivationStatusRequest buildActivationStatusRequest() {
        return new ActivationStatusRequest(true);
    }

    public static TraineeGetResponse buildTraineeGetResponse() {
        TraineeGetResponse response = new TraineeGetResponse();
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setAddress(ADDRESS);
        response.isActive(true);
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    public static TraineeAssignedTrainersUpdateRequest buildTraineeAssignedTrainersUpdateRequest() {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest();
        request.setTrainerUsernames(List.of(TRAINER_USERNAME));

        return request;
    }

    public static TraineeAssignedTrainersUpdateResponse buildTraineeAssignedTrainersUpdateResponse() {
        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    public static Trainee buildTrainee() {
        return Trainee.builder()
                .user(buildTraineeUser())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    public static User buildTraineeUser() {
        return User.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    public static Trainee buildSavedTrainee() {
        return buildTrainee().toBuilder()
                .id(VALID_ID)
                .user(buildTraineeUser())
                .build();
    }

    public static TraineeUpdateDTO buildNonExistentTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .username(NOT_FOUND_USERNAME)
                .build();
    }

    public static TrainerAssignmentUpdateDTO buildValidTrainerAssignmentUpdateDto() {
        return TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsernames(List.of(TRAINER_USERNAME1, TRAINER_USERNAME2))
                .build();
    }

    public static Trainer buildTrainer(Long id, String username) {
        return Trainer.builder()
                .id(id)
                .user(User.builder().id(id).username(username).build())
                .build();
    }

    public static Trainee buildTraineeWithTrainers(Set<Trainer> trainers) {
        Training training = Training.builder()
                .id(1L)
                .trainingName(TRAINING_NAME)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .trainingType(buildTrainingType())
                .trainee(buildTrainee())
                .trainer(buildTrainer())
                .build();

        return buildTrainee().toBuilder()
                .trainers(trainers)
                .trainings(Set.of(training))
                .build();
    }

    public static TrainerInfoDTO buildTrainerInfoDTO(String username) {
        return TrainerInfoDTO.builder()
                .username(username)
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true)
                .build();
    }

    public static TrainerCreateRequest buildTrainerCreateRequest() {
        TrainerCreateRequest request = new TrainerCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setSpecialization(SPECIALIZATION);

        return request;
    }

    public static TrainerCreateResponse buildTrainerCreateResponse() {
        TrainerCreateResponse response = new TrainerCreateResponse();
        response.setUsername(TRAINER_USERNAME);
        response.password(ENCODED_PASSWORD);

        return response;
    }

    public static Trainer buildTrainer() {
        return Trainer.builder()
                .user(buildTrainerUser())
                .specialization(buildTrainingType())
                .build();
    }

    public static TrainingType buildTrainingType() {
        return TrainingType.builder().trainingTypeName(SPECIALIZATION).build();
    }

    public static User buildTrainerUser() {
        return User.builder()
                .id(VALID_ID)
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .username(TRAINER_USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    public static Trainer buildSavedTrainer() {
        return buildTrainer().toBuilder()
                .id(VALID_ID)
                .user(buildTrainerUser())
                .build();
    }

    public static TrainerUpdateDTO buildNonExistentTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .username(NOT_FOUND_USERNAME)
                .build();
    }

    public static TrainerInfoDTO buildNotAssignedTrainerInfoDTO(String username) {
        return TrainerInfoDTO.builder().username(username).build();
    }

    public static TrainerUpdateRequest buildTrainerUpdateRequest() {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setSpecialization(SPECIALIZATION);
        request.isActive(true);

        return request;
    }

    public static TrainerUpdateResponse buildTrainerUpdateResponse() {
        TrainerUpdateResponse response = new TrainerUpdateResponse();
        response.setUsername(TRAINER_USERNAME);
        response.setFirstName(TRAINER_FIRST_NAME);
        response.setLastName(TRAINER_LAST_NAME);
        response.setSpecialization(SPECIALIZATION);
        response.isActive(true);
        response.setTrainees(List.of(buildAssignedTraineeResponse()));

        return response;
    }

    public static AssignedTraineeResponse buildAssignedTraineeResponse() {
        AssignedTraineeResponse response = new AssignedTraineeResponse();
        response.setUsername(USERNAME);
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);

        return response;
    }

    public static TrainerGetResponse buildTrainerGetResponse() {
        TrainerGetResponse response = new TrainerGetResponse();
        response.setFirstName(TRAINER_FIRST_NAME);
        response.setLastName(TRAINER_LAST_NAME);
        response.setSpecialization(SPECIALIZATION);
        response.isActive(true);
        response.setTrainees(List.of(buildAssignedTraineeResponse()));

        return response;
    }

    public static TrainingCreateRequest buildTrainingCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeUsername(USERNAME);
        request.setTrainerUsername(TRAINER_USERNAME);
        request.setTrainingDate(TRAINING_DATE);
        request.setTrainingDuration(TRAINING_DURATION);
        request.setTrainingName(TRAINING_NAME);

        return request;
    }

    public static GetTraineeTrainingResponse buildGetTraineeTrainingResponse() {
        GetTraineeTrainingResponse response = new GetTraineeTrainingResponse();
        response.setTrainerName(TRAINER_USERNAME + " " + TRAINER_LAST_NAME);
        response.setTrainingDate(TRAINING_DATE);
        response.setTrainingDuration(TRAINING_DURATION);
        response.setTrainingName(TRAINING_NAME);

        return response;
    }

    public static GetTrainerTrainingResponse buildGetTrainerTrainingResponse() {
        GetTrainerTrainingResponse response = new GetTrainerTrainingResponse();
        response.setTraineeName(FIRST_NAME + " " + LAST_NAME);
        response.setTrainingDate(TRAINING_DATE);
        response.setTrainingDuration(TRAINING_DURATION);
        response.setTrainingName(TRAINING_NAME);

        return response;
    }

    public static Training buildTraining() {
        return Training.builder()
                .trainingName(TRAINING_NAME)
                .trainingType(buildTrainingType())
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .trainee(buildTrainee())
                .trainer(buildTrainer())
                .build();
    }

    public static Training buildSavedTraining() {
        return buildTraining().toBuilder()
                .id(VALID_ID)
                .build();
    }

    public static TrainingTypeDTO buildTrainingTypeDTO() {
        return TrainingTypeDTO.builder()
                .id(VALID_ID)
                .trainingTypeName(SPECIALIZATION)
                .build();
    }

    public static TrainingTypeResponse buildTrainingTypeResponse() {
        TrainingTypeResponse response = new TrainingTypeResponse();
        response.setId(1);
        response.setName(SPECIALIZATION);

        return response;
    }

    public static PasswordChangeRequest buildInvalidPasswordChangeRequest() {
        return PasswordChangeRequest.builder()
                .username(NOT_FOUND_USERNAME)
                .oldPassword(PASSWORD)
                .newPassword(NEW_PASSWORD)
                .build();
    }

    public static ToggleActiveRequestDTO buildToggleActiveRequest() {
        return ToggleActiveRequestDTO.builder()
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    public static ToggleActiveRequestDTO buildToggleActiveRequestNonExistent() {
        return ToggleActiveRequestDTO.builder()
                .username(NOT_FOUND_USERNAME)
                .build();
    }

    public static Training buildExpectedTraining() {
        return Training.builder()
                .id(1L)
                .trainingName("Hot Yoga")
                .trainingDate(LocalDate.of(2026, 4, 15))
                .trainingDuration(60)
                .trainingType(buildTrainingType())
                .trainee(buildTrainee())
                .trainer(buildTrainer())
                .build();
    }

    public static List<Training> buildExpectedTrainings() {
        User user = User.builder()
                .id(3L)
                .firstName("Ellis")
                .lastName("Hargrove")
                .username("Ellis.Hargrove")
                .password("pass333")
                .isActive(true)
                .build();
        Trainee trainee = Trainee.builder()
                .id(2L)
                .user(user)
                .dateOfBirth(LocalDate.of(2002, 7, 15))
                .build();
        Training training = Training.builder()
                .id(2L)
                .trainingName("Hot Yoga")
                .trainingDate(LocalDate.of(2026, 4, 20))
                .trainingDuration(60)
                .trainingType(buildTrainingType())
                .trainee(trainee)
                .trainer(buildTrainer())
                .build();

        return List.of(buildExpectedTraining(), training);
    }

    public static AuthRequestDTO buildAuthRequestDTOWithInvalidPassword() {
        return AuthRequestDTO.builder()
                .username(USERNAME)
                .password(INVALID_PASSWORD)
                .build();
    }

    public static LoginResponse buildLoginResponse() {
        return new LoginResponse(USERNAME, TOKEN);
    }

    public static UserDetails buildUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .authorities(Collections.emptyList())
                .disabled(false)
                .build();
    }

    public static TrainerWorkloadMessage buildTrainerWorkloadMessage(com.gym.crm.core.messaging.ActionType actionType) {
        Training training = buildExpectedTraining();
        User user = training.getTrainer().getUser();

        return TrainerWorkloadMessage.builder()
                .trainerUsername(user.getUsername())
                .trainerFirstName(user.getFirstName())
                .trainerLastName(user.getLastName())
                .isActive(user.getIsActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();
    }
}
