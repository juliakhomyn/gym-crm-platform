package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.exception.InvalidMessageException;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMessageValidator {

    public void validate(TrainerWorkloadMessage message) {
        if (message == null) {
            throw new InvalidMessageException("Message cannot be null");
        }
        if (message.getTrainerUsername() == null || message.getTrainerUsername().isBlank()) {
            throw new InvalidMessageException("Trainer username is missing");
        }
        if (message.getTrainerFirstName() == null || message.getTrainerFirstName().isBlank()) {
            throw new InvalidMessageException("Trainer first name is missing");
        }
        if (message.getTrainerLastName() == null || message.getTrainerLastName().isBlank()) {
            throw new InvalidMessageException("Trainer last name is missing");
        }
        if (message.getIsActive() == null) {
            throw new InvalidMessageException("Is active flag is missing");
        }
        if (message.getActionType() == null) {
            throw new InvalidMessageException("Action type is missing");
        }
        if (message.getTrainingDate() == null) {
            throw new InvalidMessageException("Training date is missing");
        }
        if (message.getTrainingDuration() == null || message.getTrainingDuration() <= 0) {
            throw new InvalidMessageException("Training duration is missing or invalid");
        }
    }
}
