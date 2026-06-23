package com.gym.crm.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.core.config.RestControllerSecurityConfig;
import com.gym.crm.core.exception.ApiError;
import com.gym.crm.core.facade.GymFacade;
import com.gym.crm.core.security.CustomUserDetailsService;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@Import(RestControllerSecurityConfig.class)
class TrainingControllerTest {
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String BASE_URL = "/api/v1/trainings";

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
    @WithMockUser(username = TRAINER_USERNAME)
    void addTraining_shouldReturnOk_whenValid() throws Exception {
        TrainingCreateRequest request = TestDataProvider.buildTrainingCreateRequest();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).createTraining(any(TrainingCreateRequest.class));
    }

    @Test
    @WithMockUser(username = TRAINER_USERNAME)
    void addTraining_shouldReturnBadRequest_whenRequiredFieldsMissing() throws Exception {
        TrainingCreateRequest request = TestDataProvider.buildTrainingCreateRequest();
        request.setTraineeUsername(null);

        String content = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Validation error: traineeUsername must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    @WithMockUser(username = TRAINER_USERNAME)
    void addTraining_shouldReturnBadRequest_whenDurationNegative() throws Exception {
        TrainingCreateRequest request = TestDataProvider.buildTrainingCreateRequest();
        request.setTrainingDuration(-1);

        String content = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Validation error: trainingDuration must be greater than or equal to 1");
        verifyNoInteractions(facade);
    }

    @Test
    @WithMockUser(username = TRAINER_USERNAME)
    void getTrainingTypes_shouldReturnTrainingTypes_whenExist() throws Exception {
        List<TrainingTypeResponse> response = List.of(TestDataProvider.buildTrainingTypeResponse());

        when(facade.getTrainingTypes()).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(response.getFirst().getId()))
                .andExpect(jsonPath("$[0].name").value(response.getFirst().getName()));
        verify(facade).getTrainingTypes();
    }
}
