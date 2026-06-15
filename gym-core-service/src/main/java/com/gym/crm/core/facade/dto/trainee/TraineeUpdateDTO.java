package com.gym.crm.core.facade.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TraineeUpdateDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private final String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 110, message = "Username must be between 3 and 110 characters long")
    private final String username;

    @Past(message = "Date of birth must be in the past")
    private final LocalDate dateOfBirth;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private final String address;

    @NotNull(message = "Is active is required")
    private final Boolean isActive;
}
