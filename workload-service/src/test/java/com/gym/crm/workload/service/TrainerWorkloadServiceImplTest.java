package com.gym.crm.workload.service;

import com.gym.crm.workload.dto.TrainerWorkloadUpdateDTO;
import com.gym.crm.workload.exception.EntityNotFoundException;
import com.gym.crm.workload.model.TrainerWorkload;
import com.gym.crm.workload.repository.TrainerWorkloadRepository;
import com.gym.crm.workload.service.impl.TrainerWorkloadServiceImpl;
import com.gym.crm.workload.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gym.crm.workload.dto.ActionType.ADD;
import static com.gym.crm.workload.dto.ActionType.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {
    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";
    private static final int DURATION = 60;
    private static final int YEAR = 2026;

    private final TrainerWorkload workload = TestDataProvider.buildTrainerWorkload();

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    @Test
    void update_shouldAddWorkload_whenNonExistentWorkload() {
        TrainerWorkloadUpdateDTO dto = TestDataProvider.buildTrainerWorkloadUpdateDTO(ADD);
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);

        when(repository.findByTrainerUsername(USERNAME)).thenReturn(Optional.empty());

        service.update(dto);

        verify(repository).save(captor.capture());
        TrainerWorkload saved = captor.getValue();
        assertThat(saved.getTrainerUsername()).isEqualTo(USERNAME);
        assertThat(saved.getTrainerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(saved.getTrainerLastName()).isEqualTo(LAST_NAME);
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getYears()).hasSize(1);
        assertThat(saved.getYears().getFirst().getYear()).isEqualTo(YEAR);
        assertThat(saved.getYears().getFirst().getMonths()).hasSize(1);
        assertThat(saved.getYears().getFirst().getMonths().getFirst().getMonth()).isEqualTo(1);
        assertThat(saved.getYears().getFirst().getMonths().getFirst().getTrainingDuration()).isEqualTo(DURATION);
    }

    @Test
    void update_shouldAddWorkload_whenTrainerWorkloadExists() {
        TrainerWorkloadUpdateDTO dto = TestDataProvider.buildTrainerWorkloadUpdateDTO(ADD);
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);

        when(repository.findByTrainerUsername(USERNAME)).thenReturn(Optional.of(workload));

        service.update(dto);

        verify(repository).save(captor.capture());
        TrainerWorkload saved = captor.getValue();
        assertThat(saved.getYears()).hasSize(1);
        assertThat(saved.getYears().getFirst().getMonths()).hasSize(1);
        assertThat(saved.getYears().getFirst().getMonths().getFirst().getTrainingDuration()).isEqualTo(120);
    }

    @Test
    void update_shouldDeleteWorkload_whenTrainerWorkloadExists() {
        TrainerWorkloadUpdateDTO dto = TestDataProvider.buildTrainerWorkloadUpdateDTO(DELETE);
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);

        when(repository.findByTrainerUsername(USERNAME)).thenReturn(Optional.of(workload));

        service.update(dto);

        verify(repository).save(captor.capture());
        TrainerWorkload saved = captor.getValue();
        assertThat(saved.getYears()).isEmpty();
    }

    @Test
    void update_shouldContinue_whenDeleteNonExistentWorkload() {
        TrainerWorkloadUpdateDTO dto = TestDataProvider.buildTrainerWorkloadUpdateDTO(DELETE);

        when(repository.findByTrainerUsername(USERNAME)).thenReturn(Optional.empty());

        service.update(dto);

        verify(repository, never()).save(any());
    }

    @Test
    void getWorkingHours_shouldReturnWorkingHours_whenExist() {
        when(repository.findByTrainerUsername(USERNAME)).thenReturn(Optional.of(workload));

        int actual = service.getWorkingHours(USERNAME, YEAR, 1);

        assertThat(actual).isEqualTo(DURATION);
    }

    @Test
    void getWorkingHours_shouldThrow_whenNonExistentWorkload() {
        when(repository.findByTrainerUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.getWorkingHours(USERNAME, YEAR, 1));

        assertThat(ex.getMessage()).contains("Trainer workload not found by username");
    }
}
