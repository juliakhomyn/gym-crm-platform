package com.gym.crm.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.workload.dto.ActionType;
import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.mapper.TrainerWorkloadMapper;
import com.gym.crm.workload.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workload.service.TrainerWorkloadService;
import com.gym.crm.workload.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.gym.crm.workload.dto.ActionType.ADD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerWorkloadRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerWorkloadRestControllerTest {

    private static final String BASE_URL = "/api/v1/trainers/workload";
    private static final String USERNAME = "Callum.Whitfield";
    private static final int YEAR = 2026;
    private static final int MONTH = 1;
    private static final int DURATION = 60;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerWorkloadService service;

    @MockBean
    private TrainerWorkloadMapper mapper;

    @Test
    void findTrainerWorkload_shouldReturnWorkload() throws Exception {
        when(service.getWorkingHours(USERNAME, YEAR, MONTH)).thenReturn(DURATION);

        mockMvc.perform(get(BASE_URL + "/{username}", USERNAME)
                        .param("year", String.valueOf(YEAR))
                        .param("month", String.valueOf(MONTH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value(USERNAME))
                .andExpect(jsonPath("$.year").value(YEAR))
                .andExpect(jsonPath("$.month").value(MONTH))
                .andExpect(jsonPath("$.trainingDuration").value(DURATION));

        verify(service).getWorkingHours(USERNAME, YEAR, MONTH);
    }

    @Test
    void updateTrainerWorkload_shouldUpdateWorkload() throws Exception {
        TrainerWorkloadRequest request = TestDataProvider.buildTrainerWorkloadRequest();
        TrainerWorkloadUpdateDTO dto = TestDataProvider.buildTrainerWorkloadUpdateDTO(ADD);

        when(mapper.toUpdateDTO(any(TrainerWorkloadRequest.class))).thenReturn(dto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(mapper).toUpdateDTO(any(TrainerWorkloadRequest.class));
        verify(service).update(dto);
    }

    @Test
    void findTrainerWorkload_shouldReturnBadRequest_whenYearMissing() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{username}", USERNAME)
                        .param("month", String.valueOf(MONTH)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findTrainerWorkload_shouldReturnBadRequest_whenMonthMissing() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{username}", USERNAME)
                        .param("year", String.valueOf(YEAR)))
                .andExpect(status().isBadRequest());
    }
}