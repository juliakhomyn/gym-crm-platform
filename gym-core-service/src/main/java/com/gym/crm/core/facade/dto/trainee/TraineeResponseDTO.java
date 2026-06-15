package com.gym.crm.core.facade.dto.trainee;

import com.gym.crm.core.facade.dto.trainer.AssignedTrainerDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class TraineeResponseDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    @ToString.Exclude
    private final String password;
    private final LocalDate dateOfBirth;
    private final String address;
    private final Boolean isActive;
    private final List<AssignedTrainerDTO> assignedTrainers;
}
