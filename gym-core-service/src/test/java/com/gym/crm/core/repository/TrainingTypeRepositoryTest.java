package com.gym.crm.core.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.core.model.TrainingType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/training-type.xml", cleanBefore = true)
class TrainingTypeRepositoryTest extends AbstractRepositoryTest<TrainingTypeRepository> {

    private static final String YOGA = "Yoga";
    private static final String NOT_FOUND = "Non-Existing";

    @Test
    void findById_shouldReturnTrainingType_whenExists() {
        Optional<TrainingType> actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainingTypeName()).isEqualTo(YOGA);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<TrainingType> actual = repository.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByTrainingTypeName_shouldReturnTrainingType_whenExists() {
        Optional<TrainingType> actual = repository.findByTrainingTypeName(YOGA);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainingTypeName()).isEqualTo(YOGA);
    }

    @Test
    void findByTrainingTypeName_shouldReturnEmptyOptional_whenNotFound() {
        Optional<TrainingType> actual = repository.findByTrainingTypeName(NOT_FOUND);

        assertThat(actual).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllTrainingTypes_whenExist() {
        List<TrainingType> actual = repository.findAll();

        assertThat(actual)
                .hasSize(3)
                .extracting(TrainingType::getTrainingTypeName)
                .containsExactlyInAnyOrder("Yoga", "Pilates", "Cardio");
    }
}
