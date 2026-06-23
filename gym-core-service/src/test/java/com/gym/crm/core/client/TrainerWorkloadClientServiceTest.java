package com.gym.crm.core.client;

import com.gia.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.core.exception.ServiceConnectionException;
import com.gym.crm.core.exception.ServiceTimeoutException;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.Month;

import static com.gia.openapi.model.ActionType.ADD;
import static com.gia.openapi.model.ActionType.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void fallbackNotifyTrainingUpdate_shouldThrowServiceTimeoutException_onSocketTimeout() {
        SocketTimeoutException cause = new SocketTimeoutException("Timeout");

        ServiceTimeoutException thrown = assertThrows(ServiceTimeoutException.class, () -> service.fallbackNotifyTrainingUpdate(training, cause));

        assertThat(thrown.getMessage()).isEqualTo("workload-service did not respond within 3s");
    }

    @Test
    void fallbackNotifyTrainingUpdate_shouldThrowServiceConnectionException_onConnectException() {
        ConnectException cause = new ConnectException("Connection refused");

        ServiceConnectionException thrown = assertThrows(ServiceConnectionException.class, () -> service.fallbackNotifyTrainingUpdate(training, cause));

        assertThat(thrown.getMessage()).isEqualTo("Cannot connect to workload-service");
    }

    @Test
    void fallbackNotifyTrainingUpdate_shouldThrowServiceConnectionException_onResourceAccessException() {
        ResourceAccessException cause = new ResourceAccessException("Service unavailable");

        ServiceConnectionException thrown = assertThrows(ServiceConnectionException.class, () -> service.fallbackNotifyTrainingUpdate(training, cause));

        assertThat(thrown.getMessage()).isEqualTo("Cannot connect to workload-service");
    }

    @Test
    void fallbackNotifyTrainingUpdate_shouldThrowGenericServiceException_onUnknownException() {
        RuntimeException cause = new RuntimeException("Unexpected");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.fallbackNotifyTrainingUpdate(training, cause));

        assertThat(thrown.getMessage()).isEqualTo("Unexpected error while communicating with workload-service");
    }

    @Test
    void fallbackNotifyTrainingUpdate_shouldReturnSameServiceException_whenAlreadyMapped() {
        ServiceTimeoutException original = new ServiceTimeoutException("workload-service did not respond within 3s");

        ServiceTimeoutException thrown = assertThrows(ServiceTimeoutException.class, () -> service.fallbackNotifyTrainingUpdate(training, original));

        assertThat(thrown).isSameAs(original);
    }
}