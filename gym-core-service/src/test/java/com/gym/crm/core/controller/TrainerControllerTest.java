package com.gym.crm.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
import com.gym.crm.core.exception.ApiError;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.UserAuthenticationException;
import com.gym.crm.core.facade.GymFacade;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import com.gym.crm.core.security.CustomUserDetailsService;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import com.gym.crm.core.utils.TestDataProvider;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {
    private static final String USERNAME = "Owen.Castleberry";
    private static final String TRAINEE_NAME = "Simone Radcliffe";
    private static final String BASE_URL = "/api/v1/trainers";

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
        TrainerCreateRequest request = TestDataProvider.buildTrainerCreateRequest();
        TrainerCreateResponse response = TestDataProvider.buildTrainerCreateResponse();

        when(facade.createTrainer(request)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));
        verify(facade).createTrainer(any(TrainerCreateRequest.class));
    }

    @Test
    void register_shouldReturnNotValid_whenFirstNameMissing() throws Exception {
        TrainerCreateRequest request = TestDataProvider.buildTrainerCreateRequest();
        request.setFirstName(null);

        String content = mockMvc.perform(post(BASE_URL + "/register")
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
    void getTrainerProfile_shouldReturnTrainer_whenExists() throws Exception {
        TrainerGetResponse response = TestDataProvider.buildTrainerGetResponse();

        when(facade.getTrainerByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.specialization").value(response.getSpecialization()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainees").isArray())
                .andExpect(jsonPath("$.trainees.length()").value(response.getTrainees().size()))
                .andExpect(jsonPath("$.trainees[0].username").value(response.getTrainees().get(0).getUsername()));
        verify(facade).getTrainerByUsername(USERNAME);
    }

    @Test
    void getTrainerProfile_shouldReturnNotFound_whenTrainerNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(facade).getTrainerByUsername(USERNAME);

        String content = mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.NOT_FOUND_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Requested data was not found: User not found");
        verify(facade).getTrainerByUsername(USERNAME);
    }

    @Test
    void updateTrainerProfile_shouldReturnResponse_whenValid() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
        TrainerUpdateResponse response = TestDataProvider.buildTrainerUpdateResponse();

        when(facade.updateTrainer(request, USERNAME)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.specialization").value(response.getSpecialization()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainees").isArray())
                .andExpect(jsonPath("$.trainees.length()").value(response.getTrainees().size()))
                .andExpect(jsonPath("$.trainees[0].username").value(response.getTrainees().get(0).getUsername()));
        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), any(String.class));
    }

    @Test
    void updateTrainerProfile_shouldReturnNotValid_whenFirstNameMissing() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
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
    void updateTrainerProfile_shouldReturnNotFound_whenTrainerNotFound() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
        doThrow(new EntityNotFoundException("User not found")).when(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));

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
        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void updateTrainerProfile_shouldReturnUnauthorized_whenNoUserAuthenticated() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
        doThrow(new UserAuthenticationException("No user authenticated")).when(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));

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
        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void updateTrainerProfile_shouldReturnDBFailure_whenPersistenceException() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
        doThrow(new PersistenceException()).when(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));

        String content = mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.DATABASE_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Unexpected database access failure");
        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void updateTrainerProfile_shouldReturnUnhandledException_whenUnexpectedError() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
        doThrow(new RuntimeException()).when(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));

        String content = mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.SERVICE_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Internal processing error");
        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), eq(USERNAME));
    }

    @Test
    void toggleActive_shouldReturnOk_whenValid() throws Exception {
        ActivationStatusRequest request = TestDataProvider.buildActivationStatusRequest();

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).toggleActiveStatus(request, USERNAME);
    }

    @Test
    void getTrainerTrainings_shouldReturnResponse_whenExist() throws Exception {
        TrainerTrainingFilter filter = TestDataProvider.buildTrainerTrainingFilter();
        List<GetTrainerTrainingResponse> response = List.of(TestDataProvider.buildGetTrainerTrainingResponse());

        when(facade.getTrainerTrainingsByFilter(any(TrainerTrainingFilter.class))).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-01-30")
                        .param("traineeName", TRAINEE_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.size()))
                .andExpect(jsonPath("$[0].trainingName").value(response.get(0).getTrainingName()))
                .andExpect(jsonPath("$[0].trainingDate").value(response.get(0).getTrainingDate().toString()))
                .andExpect(jsonPath("$[0].trainingType").value(response.get(0).getTrainingType()))
                .andExpect(jsonPath("$[0].trainingDuration").value(response.get(0).getTrainingDuration()))
                .andExpect(jsonPath("$[0].traineeName").value(response.get(0).getTraineeName()));

        ArgumentCaptor<TrainerTrainingFilter> filterCaptor = ArgumentCaptor.forClass(TrainerTrainingFilter.class);
        verify(facade).getTrainerTrainingsByFilter(filterCaptor.capture());
        TrainerTrainingFilter capturedFilter = filterCaptor.getValue();

        assertThat(capturedFilter.getFromDate()).isEqualTo(filter.getFromDate());
        assertThat(capturedFilter.getToDate()).isEqualTo(filter.getToDate());
        assertThat(capturedFilter.getJoinFullName()).isEqualTo(filter.getJoinFullName());
    }
}
