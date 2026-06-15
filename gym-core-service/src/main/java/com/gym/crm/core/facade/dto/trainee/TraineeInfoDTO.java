package com.gym.crm.core.facade.dto.trainee;

import com.gym.crm.core.facade.dto.trainer.AssignedTrainerDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TraineeInfoDTO {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final Boolean isActive;
    private final LocalDate dateOfBirth;
    private final String address;
    private final List<AssignedTrainerDTO> assignedTrainers;
}
