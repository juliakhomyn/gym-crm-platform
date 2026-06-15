package com.gym.crm.core.facade.dto.trainer;

import com.gym.crm.core.facade.dto.trainee.AssignedTraineeDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class TrainerResponseDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    @ToString.Exclude
    private final String password;
    private final String specialization;
    private final Boolean isActive;
    private final List<AssignedTraineeDTO> assignedTrainees;
}
