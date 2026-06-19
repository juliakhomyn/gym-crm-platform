package com.gym.crm.core.client;

import com.gia.openapi.model.TrainerWorkloadRequest;
import com.gia.openapi.model.ActionType;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadClientService {

    private final TrainerWorkloadClient client;

    public void notifyTrainingAdded(Training training) {
        sendUpdate(training, ActionType.ADD);
    }

    public void notifyTrainingDeleted(Training training) {
        sendUpdate(training, ActionType.DELETE);
    }

    private void sendUpdate(Training training, ActionType actionType) {
        TrainerWorkloadRequest request = buildRequest(training, actionType);

        try {
            client.updateTrainerWorkload(request);

            log.info("Workload service notified: trainer={}, action={}", request.getTrainerUsername(), actionType);
        } catch (ResourceAccessException e) {
            log.error("Workload service timeout/unreachable: trainer={}, action={}", request.getTrainerUsername(), actionType, e);
        } catch (RestClientException e) {
            log.error("Workload service call failed: trainer={}, action={}", request.getTrainerUsername(), actionType, e);
        }
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
