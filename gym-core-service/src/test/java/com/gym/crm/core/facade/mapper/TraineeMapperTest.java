package com.gym.crm.core.facade.mapper;

import com.gym.crm.core.facade.dto.trainee.TraineeRequestDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeResponseDTO;
import com.gym.crm.core.facade.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeMapperTest {
    private static final String FIRST_NAME = "Ellis";
    private static final String LAST_NAME = "Hargrove";
    private static final String USERNAME = "Ellis.Hargrove";
    private static final String PASSWORD = "encodedPassword";
    private static final String ADDRESS = "123 Oak Street";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    private final TraineeMapper mapper = Mappers.getMapper(TraineeMapper.class);

    @Test
    void toEntity_shouldMapAllFields_whenMapFromTraineeRequestDTO() {
        TraineeRequestDTO traineeRequestDTO = buildTraineeRequestDTO();

        Trainee entity = mapper.toEntity(traineeRequestDTO);

        assertEquals(FIRST_NAME, entity.getUser().getFirstName());
        assertEquals(LAST_NAME, entity.getUser().getLastName());
        assertEquals(DATE_OF_BIRTH, entity.getDateOfBirth());
        assertEquals(ADDRESS, entity.getAddress());
    }

    @Test
    void toEntity_fromTraineeUpdateDTO() {
        TraineeUpdateDTO traineeUpdateDTO = buildTraineeUpdateDTO();

        Trainee entity = mapper.toEntity(traineeUpdateDTO);

        assertEquals(USERNAME, entity.getUser().getUsername());
        assertEquals(FIRST_NAME, entity.getUser().getFirstName());
        assertEquals(LAST_NAME, entity.getUser().getLastName());
        assertEquals(DATE_OF_BIRTH, entity.getDateOfBirth());
        assertEquals(ADDRESS, entity.getAddress());
        assertEquals(true, entity.getUser().getIsActive());
    }

    @Test
    void toDto_fromTraineeEntity() {
        Trainee trainee = buildTrainee();

        TraineeResponseDTO responseDTO = mapper.toDto(trainee);

        assertEquals(USERNAME, responseDTO.getUsername());
        assertEquals(PASSWORD, responseDTO.getPassword());
        assertEquals(FIRST_NAME, responseDTO.getFirstName());
        assertEquals(LAST_NAME, responseDTO.getLastName());
        assertEquals(DATE_OF_BIRTH, responseDTO.getDateOfBirth());
        assertEquals(ADDRESS, responseDTO.getAddress());
        assertEquals(true, responseDTO.getIsActive());
    }

    private TraineeRequestDTO buildTraineeRequestDTO() {
        return TraineeRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    private TraineeUpdateDTO buildTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .isActive(true)
                .build();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .user(buildUser())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true)
                .build();
    }
}
