package com.gym.crm.core.facade.mapper;

import com.gym.crm.core.facade.dto.trainer.TrainerRequestDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerResponseDTO;
import com.gym.crm.core.facade.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.TrainingType;
import com.gym.crm.core.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainerMapperTest {
    private static final String FIRST_NAME = "Ellis";
    private static final String LAST_NAME = "Hargrove";
    private static final String USERNAME = "Ellis.Hargrove";
    private static final String PASSWORD = "encodedPassword";
    private static final String TRAINING_TYPE_NAME = "Yoga";

    private final TrainerMapper mapper = Mappers.getMapper(TrainerMapper.class);

    @Test
    void toEntity_shouldMapAllFields_whenMapFromTrainerRequestDTO() {
        TrainerRequestDTO trainerRequestDTO = buildTrainerRequestDTO();

        Trainer entity = mapper.toEntity(trainerRequestDTO);

        assertEquals(FIRST_NAME, entity.getUser().getFirstName());
        assertEquals(LAST_NAME, entity.getUser().getLastName());
    }

    @Test
    void toEntity_shouldMapAllFields_whenMapFromTrainerUpdateDTO() {
        TrainerUpdateDTO trainerUpdateDTO = buildTrainerUpdateDTO();

        Trainer entity = mapper.toEntity(trainerUpdateDTO);

        assertEquals(USERNAME, entity.getUser().getUsername());
        assertEquals(FIRST_NAME, entity.getUser().getFirstName());
        assertEquals(LAST_NAME, entity.getUser().getLastName());
        assertEquals(TRAINING_TYPE_NAME, entity.getSpecialization().getTrainingTypeName());
        assertEquals(true, entity.getUser().getIsActive());
    }

    @Test
    void toDto_shouldMapAllFields_whenMapFromTrainerEntity() {
        Trainer trainer = buildTrainer();

        TrainerResponseDTO responseDTO = mapper.toDto(trainer);

        assertEquals(USERNAME, responseDTO.getUsername());
        assertEquals(PASSWORD, responseDTO.getPassword());
        assertEquals(FIRST_NAME, responseDTO.getFirstName());
        assertEquals(LAST_NAME, responseDTO.getLastName());
        assertEquals(TRAINING_TYPE_NAME, responseDTO.getSpecialization());
        assertEquals(true, responseDTO.getIsActive());
    }

    @Test
    void mapStringToTrainingType_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.map((String) null));
    }

    @Test
    void mapTrainingTypeToString_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.map((TrainingType) null));
    }

    private TrainerRequestDTO buildTrainerRequestDTO() {
        return TrainerRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(TRAINING_TYPE_NAME)
                .build();
    }

    private TrainerUpdateDTO buildTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(TRAINING_TYPE_NAME)
                .isActive(true)
                .build();
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .user(buildUser())
                .specialization(buildTrainingType())
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

    private TrainingType buildTrainingType() {
        return TrainingType.builder().trainingTypeName(TRAINING_TYPE_NAME).build();
    }
}
