package com.gym.crm.core.messaging;

import com.gym.crm.core.model.Training;
import com.gym.crm.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMapper {

    public TrainerWorkloadMessage toMessage(Training training, ActionType actionType) {
        User user = training.getTrainer().getUser();

        return TrainerWorkloadMessage.builder()
                .trainerUsername(user.getUsername())
                .trainerFirstName(user.getFirstName())
                .trainerLastName(user.getLastName())
                .isActive(user.getIsActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();
    }
}
