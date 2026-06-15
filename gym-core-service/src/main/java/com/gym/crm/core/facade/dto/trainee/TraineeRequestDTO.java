package com.gym.crm.core.facade.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TraineeRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed characters")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed characters")
    private final String lastName;

    @Past(message = "Date of birth must be in the past")
    private final LocalDate dateOfBirth;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private final String address;
}
