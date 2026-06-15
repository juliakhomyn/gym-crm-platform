package com.gym.crm.core.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.core.model.Training;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/training.xml", cleanBefore = true)
class TrainingRepositoryTest extends AbstractRepositoryTest<TrainingRepository> {

    @Test
    void findById_shouldReturnTraining_whenExists() {
        Training expected = TestDataProvider.buildExpectedTraining();

        Optional<Training> actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainingName()).isEqualTo("Hot Yoga");
        assertThat(actual.get().getTrainingDate()).isEqualTo(LocalDate.of(2026, 4, 15));
        assertThat(actual).contains(expected);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Training> actual = repository.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllTrainings_whenExist() {
        List<Training> expected = TestDataProvider.buildExpectedTrainings();

        List<Training> actual = repository.findAll();

        assertThat(actual)
                .isNotEmpty()
                .containsAll(expected);
        assertThat(actual)
                .hasSize(2)
                .extracting(Training::getTrainingName)
                .containsExactly("Hot Yoga", "Hot Yoga");
        assertThat(actual)
                .extracting(Training::getTrainingDate)
                .containsExactlyInAnyOrder(LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 15));
        assertThat(actual)
                .extracting(Training::getTrainingDuration)
                .contains(60);
    }
}
