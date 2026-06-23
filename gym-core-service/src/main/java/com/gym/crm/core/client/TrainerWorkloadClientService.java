package com.gym.crm.core.client;

import com.gia.openapi.model.TrainerWorkloadRequest;
import com.gia.openapi.model.ActionType;
import com.gym.crm.core.exception.ServiceConnectionException;
import com.gym.crm.core.exception.ServiceException;
import com.gym.crm.core.exception.ServiceTimeoutException;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.model.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadClientService {
    private static final String WORKLOAD_SERVICE = "workload-service";

    private final TrainerWorkloadClient client;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackNotifyTrainingUpdate")
    public void notifyTrainingAdded(Training training) {
        sendUpdate(training, ActionType.ADD);
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackNotifyTrainingUpdate")
    public void notifyTrainingDeleted(Training training) {
        sendUpdate(training, ActionType.DELETE);
    }

    public void fallbackNotifyTrainingUpdate(Training training, Throwable t) {
        String trainerUsername = training.getTrainer() != null && training.getTrainer().getUser() != null
                ? training.getTrainer().getUser().getUsername()
                : "unknown";

        log.warn("Fallback: Could not notify workload service for training '{}', trainer '{}'. Reason: {}",
                training.getId(), trainerUsername, t.toString());

        throw mapToServiceException(t);
    }

    private RuntimeException mapToServiceException(Throwable throwable) {
        Throwable cause = getRootCause(throwable);

        if (cause instanceof ServiceException serviceException) {
            return serviceException;
        }
        if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException) {
            return new ServiceTimeoutException(WORKLOAD_SERVICE + " did not respond within 3s");
        }
        if (cause instanceof ConnectException || throwable instanceof ResourceAccessException) {
            return new ServiceConnectionException("Cannot connect to " + WORKLOAD_SERVICE);
        }

        return new RuntimeException("Unexpected error while communicating with " + WORKLOAD_SERVICE);
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable current = throwable;

        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }

        return current;
    }

    private void sendUpdate(Training training, ActionType actionType) {
        TrainerWorkloadRequest request = buildRequest(training, actionType);
        client.updateTrainerWorkload(request);

        log.info("Workload service notified: trainer={}, action={}", request.getTrainerUsername(), actionType);
    }

    private TrainerWorkloadRequest buildRequest(Training training, ActionType actionType) {
        User user = training.getTrainer().getUser();

        return new TrainerWorkloadRequest()
                .trainerUsername(user.getUsername())
                .trainerFirstName(user.getFirstName())
                .trainerLastName(user.getLastName())
                .isActive(user.getIsActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType);
    }
}
