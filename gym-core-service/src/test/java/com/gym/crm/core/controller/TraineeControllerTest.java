package com.gym.crm.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.core.exception.ApiError;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.UserAuthenticationException;
import com.gym.crm.core.exception.ValidationFailedException;
import com.gym.crm.core.facade.GymFacade;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import com.gym.crm.core.security.CustomUserDetailsService;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.core.utils.JsonUtil.readJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String TRAINER_NAME = "Owen Castleberry";
    private static final String TRAINING_TYPE = "Cardio";
    private static final String BASE_URL = "/api/v1/trainees";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade facade;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void register_shouldReturnCredentials_whenValid() throws Exception {
        String requestBody = readJson("json/trainee/trainee_create_request.json");
        String expectedResponse = readJson("json/trainee/trainee_create_response.json");
        TraineeCreateResponse response = TestDataProvider.buildTraineeCreateResponse();

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        String actualResponse = mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, true);
        verify(facade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void register_shouldReturnNotValid_whenFirstNameMissing() throws Exception {
        String requestBody = readJson("json/trainee/trainee_create_request_invalid.json");
        String expectedResponse = readJson("json/trainee/firstname_null_error_response.json");

        String actualResponse = mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, true);
        verifyNoInteractions(facade);
    }

    @Test
    void register_shouldReturnNotValid_whenDateOfBirthInTheFuture() throws Exception {
        TraineeCreateRequest request = TestDataProvider.buildTraineeCreateRequest();
        request.setDateOfBirth(LocalDate.of(2030, 1, 1));

        doThrow(new ValidationFailedException("Date of birth must be in the past")).when(facade).createTrainee(request);

        String content = mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Validation error: Date of birth must be in the past");
        verify(facade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void register_shouldReturnCredentials_whenOptionalFieldsOmitted() throws Exception {
        TraineeCreateRequest request = TestDataProvider.buildTraineeCreateRequestOnlyRequiredFields();
        TraineeCreateResponse response = TestDataProvider.buildTraineeCreateResponse();

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));
    }

    @Test
    void getTraineeProfile_shouldReturnTrainee_whenExists() throws Exception {
        TraineeGetResponse response = TestDataProvider.buildTraineeGetResponse();

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(response.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainers").isArray())
                .andExpect(jsonPath("$.trainers.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()));
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void getTraineeProfile_shouldReturnNotFound_whenTraineeNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(facade).getTraineeByUsername(USERNAME);

        String content = mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.NOT_FOUND_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Requested data was not found: User not found");
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void updateTraineeProfile_shouldReturnResponse_whenValid() throws Exception {
        TraineeUpdateRequest request = TestDataProvider.buildTraineeUpdateRequest();
        TraineeUpdateResponse response = TestDataProvider.buildTraineeUpdateResponse();

        when(facade.updateTrainee(request, USERNAME)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(response.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainers").isArray())
                .andExpect(jsonPath("$.trainers.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()));
        verify(facade).updateTrainee(any(TraineeUpdateRequest.class), any(String.class));
    }

    @Test
    void updateTraineeProfile_shouldReturnNotValid_whenFirstNameMissing() throws Exception {
        TraineeUpdateRequest request = TestDataProvider.buildTraineeUpdateRequest();
        request.setFirstName(null);

        String content = mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Validation error: firstName must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void updateTraineeProfile_shouldReturnNotFound_whenTraineeNotFound() throws Exception {
        TraineeUpdateRequest request = TestDataProvider.buildTraineeUpdateRequest();
        doThrow(new EntityNotFoundException("User not found")).when(facade).updateTrainee(any(TraineeUpdateRequest.class), eq(USERNAME));

        String content = mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.NOT_FOUND_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Requested data was not found: User not found");
        verify(facade).updateTrainee(any(TraineeUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void updateTraineeProfile_shouldReturnUnauthorized_whenNoUserAuthenticated() throws Exception {
        TraineeUpdateRequest request = TestDataProvider.buildTraineeUpdateRequest();
        doThrow(new UserAuthenticationException("No user authenticated")).when(facade).updateTrainee(any(TraineeUpdateRequest.class), eq(USERNAME));

        String content = mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.AUTHENTICATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Authentication fails: No user authenticated");
        verify(facade).updateTrainee(any(TraineeUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void deleteTrainee_shouldDeleteTrainee_whenExists() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void deleteTrainee_shouldReturnNotFound_whenTraineeNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(facade).deleteTraineeByUsername(USERNAME);

        String content = mockMvc.perform(delete(BASE_URL + "/" + USERNAME))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.NOT_FOUND_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Requested data was not found: User not found");
        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void toggleActive_shouldChangeIsActive_whenValid() throws Exception {
        ActivationStatusRequest request = TestDataProvider.buildActivationStatusRequest();

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).toggleActiveStatus(request, USERNAME);
    }

    @Test
    void updateTraineeTrainers_shouldReturnList_whenValid() throws Exception {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest(List.of(TRAINER_USERNAME));
        TraineeAssignedTrainersUpdateResponse response = TestDataProvider.buildTraineeAssignedTrainersUpdateResponse();

        when(facade.updateTraineeTrainersList(request, USERNAME)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()))
                .andExpect(jsonPath("$.trainers[0].firstName").value(response.getTrainers().get(0).getFirstName()))
                .andExpect(jsonPath("$.trainers[0].lastName").value(response.getTrainers().get(0).getLastName()))
                .andExpect(jsonPath("$.trainers[0].specialization").value(response.getTrainers().get(0).getSpecialization()));
        verify(facade).updateTraineeTrainersList(request, USERNAME);
    }

    @Test
    void updateTraineeTrainers_shouldReturnUnauthorized_whenNoUserAuthenticated() throws Exception {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest(List.of(TRAINER_USERNAME));
        doThrow(new UserAuthenticationException("No user authenticated")).when(facade).updateTraineeTrainersList(any(TraineeAssignedTrainersUpdateRequest.class), eq(USERNAME));

        String content = mockMvc.perform(put(BASE_URL + "/" + USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.AUTHENTICATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Authentication fails: No user authenticated");
        verify(facade).updateTraineeTrainersList(any(TraineeAssignedTrainersUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void getAvailableTrainers_shouldReturnList_whenValid() throws Exception {
        List<AssignedTrainerResponse> response = List.of(TestDataProvider.buildAssignedTrainerResponse());

        when(facade.getTrainersNotAssignedToTrainee(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/available-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.size()))
                .andExpect(jsonPath("$[0].username").value(response.get(0).getUsername()))
                .andExpect(jsonPath("$[0].firstName").value(response.get(0).getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(response.get(0).getLastName()))
                .andExpect(jsonPath("$[0].specialization").value(response.get(0).getSpecialization()));
        verify(facade).getTrainersNotAssignedToTrainee(USERNAME);
    }

    @Test
    void getTraineeTrainings_shouldReturnResponse_whenExist() throws Exception {
        TraineeTrainingFilter filter = TestDataProvider.buildTraineeTrainingFilter();
        List<GetTraineeTrainingResponse> response = List.of(TestDataProvider.buildGetTraineeTrainingResponse());

        when(facade.getTraineeTrainingsByFilter(any(TraineeTrainingFilter.class))).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-01-30")
                        .param("trainerName", TRAINER_NAME)
                        .param("trainingType", TRAINING_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.size()))
                .andExpect(jsonPath("$[0].trainingName").value(response.get(0).getTrainingName()))
                .andExpect(jsonPath("$[0].trainingDate").value(response.get(0).getTrainingDate().toString()))
                .andExpect(jsonPath("$[0].trainingType").value(response.get(0).getTrainingType()))
                .andExpect(jsonPath("$[0].trainingDuration").value(response.get(0).getTrainingDuration()))
                .andExpect(jsonPath("$[0].trainerName").value(response.get(0).getTrainerName()));

        ArgumentCaptor<TraineeTrainingFilter> filterCaptor = ArgumentCaptor.forClass(TraineeTrainingFilter.class);
        verify(facade).getTraineeTrainingsByFilter(filterCaptor.capture());
        TraineeTrainingFilter capturedFilter = filterCaptor.getValue();

        assertThat(capturedFilter.getFromDate()).isEqualTo(filter.getFromDate());
        assertThat(capturedFilter.getToDate()).isEqualTo(filter.getToDate());
        assertThat(capturedFilter.getJoinFullName()).isEqualTo(filter.getJoinFullName());
        assertThat(capturedFilter.getTrainingTypeName()).isEqualTo(filter.getTrainingTypeName());
    }
}
