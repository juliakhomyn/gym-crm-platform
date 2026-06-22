package com.gym.crm.core.client;

import com.gia.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.Month;

import static com.gia.openapi.model.ActionType.ADD;
import static com.gia.openapi.model.ActionType.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadClientServiceTest {
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String TRAINER_FIRST_NAME = "Owen";
    private static final String TRAINER_LAST_NAME = "Castleberry";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2024, Month.JANUARY, 15);

    private final Training training = TestDataProvider.buildTraining();

    @Mock
    private TrainerWorkloadClient client;

    @InjectMocks
    private TrainerWorkloadClientService service;

    @Test
    void notifyTrainingAdded_shouldCallClientWithAddAction() {
        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);

        service.notifyTrainingAdded(training);

        verify(client).updateTrainerWorkload(captor.capture());
        TrainerWorkloadRequest request = captor.getValue();
        assertThat(request.getTrainerUsername()).isEqualTo(TRAINER_USERNAME);
        assertThat(request.getTrainerFirstName()).isEqualTo(TRAINER_FIRST_NAME);
        assertThat(request.getTrainerLastName()).isEqualTo(TRAINER_LAST_NAME);
        assertThat(request.getIsActive()).isTrue();
        assertThat(request.getTrainingDate()).isEqualTo(TRAINING_DATE);
        assertThat(request.getTrainingDuration()).isEqualTo(60);
        assertThat(request.getActionType()).isEqualTo(ADD);
    }

    @Test
    void notifyTrainingDeleted_shouldCallClientWithDeleteAction() {
        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);

        service.notifyTrainingDeleted(training);

        verify(client).updateTrainerWorkload(captor.capture());
        TrainerWorkloadRequest request = captor.getValue();
        assertThat(request.getActionType()).isEqualTo(DELETE);
    }

    @Test
    void sendUpdate_shouldLogResourceAccessException() {
        doThrow(new ResourceAccessException("Timeout")).when(client).updateTrainerWorkload(any());

        assertThrows(RuntimeException.class, () -> service.notifyTrainingAdded(training));

        verify(client).updateTrainerWorkload(any());
    }

    @Test
    void sendUpdate_shouldLogRestClientException() {
        doThrow(new RestClientException("REST error")).when(client).updateTrainerWorkload(any());

        assertThrows(RuntimeException.class, () -> service.notifyTrainingAdded(training));

        verify(client).updateTrainerWorkload(any());
    }

    @Test
    void sendUpdate_shouldNotThrow_whenFallbackIsCalled() {
        assertDoesNotThrow(() -> service.fallbackNotifyTrainingUpdate(training, new RuntimeException("API error")));
    }
}