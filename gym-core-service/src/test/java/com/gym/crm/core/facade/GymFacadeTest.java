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
import com.gym.crm.core.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.core.facade.dto.training.TrainingTypeDTO;
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
import com.gym.crm.core.utils.TestDataProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TraineeRestMapper traineeMapper;
    @Mock
    private TrainerRestMapper trainerMapper;
    @Mock
    private TrainingRestMapper trainingMapper;

    @InjectMocks
    private GymFacade facade;

    private final TraineeRequestDTO traineeRequestDTO = TestDataProvider.buildTraineeRequestDTO();
    private final TraineeUpdateDTO traineeUpdateDTO = TestDataProvider.buildTraineeUpdateDTO();
    private final TraineeResponseDTO traineeResponseDTO = TestDataProvider.buildTraineeResponseDTO();
    private final TraineeInfoDTO traineeInfoDTO = TestDataProvider.buildTraineeInfoDTO();
    private final TrainerAssignmentUpdateDTO trainerAssignmentUpdateDTO = TestDataProvider.buildTrainerAssignmentUpdateDTO();

    private final TrainerRequestDTO trainerRequestDTO = TestDataProvider.buildTrainerRequestDTO();
    private final TrainerUpdateDTO trainerUpdateDTO = TestDataProvider.buildTrainerUpdateDTO();
    private final TrainerResponseDTO trainerResponseDTO = TestDataProvider.buildTrainerResponseDTO();
    private final TrainerInfoDTO trainerInfoDTO = TestDataProvider.buildTrainerInfoDTO();

    private final TrainingRequestDTO trainingRequestDTO = TestDataProvider.buildTrainingRequestDTO();
    private final TrainingResponseDTO trainingResponseDTO = TestDataProvider.buildTrainingResponseDTO();
    private final TraineeTrainingFilter traineeTrainingFilter = TestDataProvider.buildTraineeTrainingFilter();
    private final TrainerTrainingFilter trainerTrainingFilter = TestDataProvider.buildTrainerTrainingFilter();
    private final TrainingTypeDTO trainingTypeDTO = TestDataProvider.buildTrainingTypeDTO();

    private final ToggleActiveRequestDTO toggleActiveRequestDTO = TestDataProvider.buildToggleActiveRequestDTO();
    private final PasswordChangeRequest passwordChangeRequest = TestDataProvider.buildPasswordChangeRequest();

    private final LoginRequest loginRequest = TestDataProvider.buildLoginRequest();
    private final LoginResponse loginResponse = TestDataProvider.buildLoginResponse();
    private final LoginChangeRequest loginChangeRequest = TestDataProvider.buildLoginChangeRequest();

    private final AuthRequestDTO authRequestDTO = TestDataProvider.buildAuthRequestDTO();
    private final AuthResponseDTO authResponseDTO = TestDataProvider.buildAuthResponseDTO();
    private final ActivationStatusRequest activationStatusRequest = TestDataProvider.buildActivationStatusRequest();

    private final TraineeCreateRequest traineeCreateRequest = TestDataProvider.buildTraineeCreateRequest();
    private final TraineeCreateResponse traineeCreateResponse = TestDataProvider.buildTraineeCreateResponse();
    private final TraineeUpdateRequest traineeUpdateRequest = TestDataProvider.buildTraineeUpdateRequest();
    private final TraineeUpdateResponse traineeUpdateResponse = TestDataProvider.buildTraineeUpdateResponse();
    private final TraineeGetResponse traineeGetResponse = TestDataProvider.buildTraineeGetResponse();
    private final TraineeAssignedTrainersUpdateRequest traineeAssignedTrainersUpdateRequest = TestDataProvider.buildTraineeAssignedTrainersUpdateRequest();
    private final TraineeAssignedTrainersUpdateResponse traineeAssignedTrainersUpdateResponse = TestDataProvider.buildTraineeAssignedTrainersUpdateResponse();

    private final TrainerCreateRequest trainerCreateRequest = TestDataProvider.buildTrainerCreateRequest();
    private final TrainerCreateResponse trainerCreateResponse = TestDataProvider.buildTrainerCreateResponse();
    private final TrainerUpdateRequest trainerUpdateRequest = TestDataProvider.buildTrainerUpdateRequest();
    private final TrainerUpdateResponse trainerUpdateResponse = TestDataProvider.buildTrainerUpdateResponse();
    private final AssignedTrainerResponse assignedTrainerResponse = TestDataProvider.buildAssignedTrainerResponse();
    private final TrainerGetResponse trainerGetResponse = TestDataProvider.buildTrainerGetResponse();

    private final TrainingCreateRequest trainingCreateRequest = TestDataProvider.buildTrainingCreateRequest();
    private final GetTraineeTrainingResponse getTraineeTrainingResponse = TestDataProvider.buildGetTraineeTrainingResponse();
    private final GetTrainerTrainingResponse getTrainerTrainingResponse = TestDataProvider.buildGetTrainerTrainingResponse();
    private final TrainingTypeResponse trainingTypeResponse = TestDataProvider.buildTrainingTypeResponse();

    @Test
    void login_shouldSaveUserToContextAndReturnResponseDTO() {
        when(authenticationService.authenticate(authRequestDTO)).thenReturn(authResponseDTO);

        LoginResponse actual = facade.login(loginRequest);

        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isEqualTo(loginResponse.getUsername());
        assertThat(actual.getToken()).isEqualTo(loginResponse.getToken());
    }

    @Test
    void logout_shouldCallClearContext() {
        HttpServletRequest request = new MockHttpServletRequest();

        facade.logout(request);

        verify(authenticationService).logout(request);
    }

    @Test
    void createTrainee_shouldReturnResponseDTO() {
        when(traineeMapper.toDto(traineeCreateRequest)).thenReturn(traineeRequestDTO);
        when(traineeService.createTrainee(traineeRequestDTO)).thenReturn(traineeResponseDTO);
        when(traineeMapper.toRest(traineeResponseDTO)).thenReturn(traineeCreateResponse);

        TraineeCreateResponse actual = facade.createTrainee(traineeCreateRequest);

        assertThat(actual).isEqualTo(traineeCreateResponse);
        verify(traineeService).createTrainee(traineeRequestDTO);
    }

    @Test
    void updateTrainee_shouldReturnResponseDTO() {
        when(traineeMapper.toDto(USERNAME, traineeUpdateRequest)).thenReturn(traineeUpdateDTO);
        when(traineeService.updateTrainee(traineeUpdateDTO)).thenReturn(traineeResponseDTO);
        when(traineeMapper.toRestUpdateResponse(traineeResponseDTO)).thenReturn(traineeUpdateResponse);

        TraineeUpdateResponse actual = facade.updateTrainee(traineeUpdateRequest, USERNAME);

        assertThat(actual).isEqualTo(traineeUpdateResponse);
        verify(traineeService).updateTrainee(traineeUpdateDTO);
    }

    @Test
    void toggleActiveStatus_shouldCallUserService() {
        facade.toggleActiveStatus(activationStatusRequest, USERNAME);

        ArgumentCaptor<ToggleActiveRequestDTO> dtoCaptor = ArgumentCaptor.forClass(ToggleActiveRequestDTO.class);
        verify(userService).toggleActive(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue()).isEqualTo(toggleActiveRequestDTO);
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteTrainee() {
        facade.deleteTraineeByUsername(USERNAME);

        verify(traineeService).deleteByUsername(USERNAME);
    }

    @Test
    void getTraineeByUsername_shouldReturnInfoDTO() {
        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(traineeInfoDTO);
        when(traineeMapper.toRest(traineeInfoDTO)).thenReturn(traineeGetResponse);

        TraineeGetResponse actual = facade.getTraineeByUsername(USERNAME);

        assertThat(actual).isEqualTo(traineeGetResponse);
        verify(traineeService).getTraineeByUsername(USERNAME);
    }

    @Test
    void getAllTrainees_shouldReturnListOfInfoDTOs() {
        when(traineeService.getAllTrainees()).thenReturn(List.of(traineeInfoDTO));
        when(traineeMapper.toRest(traineeInfoDTO)).thenReturn(traineeGetResponse);

        List<TraineeGetResponse> actual = facade.getAllTrainees();

        assertThat(actual)
                .hasSize(1)
                .contains(traineeGetResponse);
        verify(traineeService).getAllTrainees();
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList_whenNoTrainees() {
        when(traineeService.getAllTrainees()).thenReturn(List.of());

        List<TraineeGetResponse> actual = facade.getAllTrainees();

        assertThat(actual).isEmpty();
        verify(traineeService).getAllTrainees();
    }

    @Test
    void updateTraineeTrainersList_shouldCallService() {
        when(traineeService.updateTrainersList(trainerAssignmentUpdateDTO)).thenReturn(List.of(trainerInfoDTO));
        when(trainerMapper.toRest(trainerInfoDTO)).thenReturn(assignedTrainerResponse);

        TraineeAssignedTrainersUpdateResponse actual = facade.updateTraineeTrainersList(traineeAssignedTrainersUpdateRequest, USERNAME);

        assertThat(actual).isEqualTo(traineeAssignedTrainersUpdateResponse);
        verify(traineeService).updateTrainersList(trainerAssignmentUpdateDTO);
    }

    @Test
    void createTrainer_shouldReturnResponseDTO() {
        when(trainerMapper.toDto(trainerCreateRequest)).thenReturn(trainerRequestDTO);
        when(trainerService.createTrainer(trainerRequestDTO)).thenReturn(trainerResponseDTO);
        when(trainerMapper.toRest(trainerResponseDTO)).thenReturn(trainerCreateResponse);

        TrainerCreateResponse actual = facade.createTrainer(trainerCreateRequest);

        assertThat(actual).isEqualTo(trainerCreateResponse);
        verify(trainerService).createTrainer(trainerRequestDTO);
    }

    @Test
    void updateTrainer_shouldReturnResponseDTO() {
        when(trainerMapper.toDto(TRAINER_USERNAME, trainerUpdateRequest)).thenReturn(trainerUpdateDTO);
        when(trainerService.updateTrainer(trainerUpdateDTO)).thenReturn(trainerResponseDTO);
        when(trainerMapper.toRestUpdateResponse(trainerResponseDTO)).thenReturn(trainerUpdateResponse);

        TrainerUpdateResponse actual = facade.updateTrainer(trainerUpdateRequest, TRAINER_USERNAME);

        assertThat(actual).isEqualTo(trainerUpdateResponse);
        verify(trainerService).updateTrainer(trainerUpdateDTO);
    }

    @Test
    void getTrainerByUsername_shouldReturnInfoDTO() {
        when(trainerService.getTrainerByUsername(TRAINER_USERNAME)).thenReturn(trainerInfoDTO);
        when(trainerMapper.toRestGetResponse(trainerInfoDTO)).thenReturn(trainerGetResponse);

        TrainerGetResponse actual = facade.getTrainerByUsername(TRAINER_USERNAME);

        assertThat(actual).isEqualTo(trainerGetResponse);
        verify(trainerService).getTrainerByUsername(TRAINER_USERNAME);
    }

    @Test
    void getAllTrainers_shouldReturnListOfInfoDTOs() {
        when(trainerService.getAllTrainers()).thenReturn(List.of(trainerInfoDTO));
        when(trainerMapper.toRestGetResponse(trainerInfoDTO)).thenReturn(trainerGetResponse);

        List<TrainerGetResponse> actual = facade.getAllTrainers();

        assertThat(actual)
                .hasSize(1)
                .contains(trainerGetResponse);
        verify(trainerService).getAllTrainers();
    }

    @Test
    void getAllTrainers_shouldReturnEmptyList_whenNoTrainers() {
        when(trainerService.getAllTrainers()).thenReturn(List.of());

        List<TrainerGetResponse> actual = facade.getAllTrainers();

        assertThat(actual).isEmpty();
        verify(trainerService).getAllTrainers();
    }

    @Test
    void getTrainersNotAssignedToTrainee_shouldReturnListOfInfoDTOs() {
        when(trainerService.getNotAssignedToTrainee(USERNAME)).thenReturn(List.of(trainerInfoDTO));
        when(trainerMapper.toRest(trainerInfoDTO)).thenReturn(assignedTrainerResponse);

        List<AssignedTrainerResponse> actual = facade.getTrainersNotAssignedToTrainee(USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(assignedTrainerResponse);
        verify(trainerService).getNotAssignedToTrainee(USERNAME);
    }

    @Test
    void changePassword_shouldCallUserService() {
        facade.changePassword(loginChangeRequest);

        ArgumentCaptor<PasswordChangeRequest> dtoCaptor = ArgumentCaptor.forClass(PasswordChangeRequest.class);
        verify(userService).changePassword(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue()).isEqualTo(passwordChangeRequest);
    }

    @Test
    void createTraining_shouldSaveTraining() {
        when(trainingMapper.toDto(trainingCreateRequest)).thenReturn(trainingRequestDTO);

        facade.createTraining(trainingCreateRequest);

        ArgumentCaptor<TrainingRequestDTO> dtoCaptor = ArgumentCaptor.forClass(TrainingRequestDTO.class);
        verify(trainingService).createTraining(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue()).isEqualTo(trainingRequestDTO);
    }

    @Test
    void getTraineeTrainingsByFilter_shouldReturnListOfGetTrainingResponse() {
        when(trainingMapper.toRestTraineeResponse(trainingResponseDTO)).thenReturn(getTraineeTrainingResponse);
        when(trainingService.getTraineeTrainings(traineeTrainingFilter)).thenReturn(List.of(trainingResponseDTO));

        List<GetTraineeTrainingResponse> actual = facade.getTraineeTrainingsByFilter(traineeTrainingFilter);

        assertThat(actual)
                .hasSize(1)
                .contains(getTraineeTrainingResponse);
        verify(trainingService).getTraineeTrainings(traineeTrainingFilter);
    }

    @Test
    void getTrainerTrainingsByFilter_shouldReturnListOfTrainings() {
        when(trainingMapper.toRestTrainerResponse(trainingResponseDTO)).thenReturn(getTrainerTrainingResponse);
        when(trainingService.getTrainerTrainings(trainerTrainingFilter)).thenReturn(List.of(trainingResponseDTO));

        List<GetTrainerTrainingResponse> actual = facade.getTrainerTrainingsByFilter(trainerTrainingFilter);

        assertThat(actual)
                .hasSize(1)
                .contains(getTrainerTrainingResponse);
        verify(trainingService).getTrainerTrainings(trainerTrainingFilter);
    }

    @Test
    void getAllTrainingTypes_shouldReturnTrainingTypeList() {
        when(trainingService.getAllTrainingTypes()).thenReturn(List.of(trainingTypeDTO));
        when(trainingMapper.toRest(trainingTypeDTO)).thenReturn(trainingTypeResponse);

        List<TrainingTypeResponse> actual = facade.getTrainingTypes();

        assertThat(actual)
                .hasSize(1)
                .contains(trainingTypeResponse);
        verify(trainingService).getAllTrainingTypes();
    }
}
