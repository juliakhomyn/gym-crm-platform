package com.gym.crm.workload.messaging;

import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import com.gym.crm.workload.exception.InvalidMessageException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessageValidator {
    private static final String DELIMITER = "; ";
    private static final String MESSAGE_NOT_NULL = "Message cannot be null";

    private final Validator validator;

    public void validate(TrainerWorkloadMessage workloadMessage) {
        if (workloadMessage == null) {
            throw new InvalidMessageException(MESSAGE_NOT_NULL);
        }
        
        Set<ConstraintViolation<TrainerWorkloadMessage>> violations = validator.validate(workloadMessage);
        if (violations.isEmpty()) {
            return;
        }

        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(DELIMITER));

        throw new InvalidMessageException(message);
    }
}
