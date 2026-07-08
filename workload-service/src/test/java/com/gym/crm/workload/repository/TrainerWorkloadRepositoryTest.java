package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.MonthWorkload;
import com.gym.crm.workload.model.TrainerWorkload;
import com.gym.crm.workload.model.YearWorkload;
import com.gym.crm.workload.utils.TestDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
class TrainerWorkloadRepositoryTest extends AbstractMongoRepositoryTest{
    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";
    private static final int DURATION = 60;
    private static final int YEAR = 2026;
    private static final int MONTH = 1;

    private final TrainerWorkload trainerWorkload = TestDataProvider.buildTrainerWorkload();

    @Autowired
    private TrainerWorkloadRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void findByTrainerUsername_shouldReturnWorkload_whenExists() {
        repository.save(trainerWorkload);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainerUsername()).isEqualTo(USERNAME);
        assertThat(actual.get().getTrainerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.get().getTrainerLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.get().getIsActive()).isTrue();
        assertThat(actual.get().getYears()).hasSize(1);

        YearWorkload year = actual.get().getYears().getFirst();
        assertThat(year.getYear()).isEqualTo(YEAR);
        assertThat(year.getMonths()).hasSize(1);

        MonthWorkload month = year.getMonths().getFirst();
        assertThat(month.getMonth()).isEqualTo(MONTH);
        assertThat(month.getTrainingDuration()).isEqualTo(DURATION);
    }

    @Test
    void findByTrainerUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<TrainerWorkload> actual = repository.findByTrainerUsername("unknown");

        assertThat(actual).isEmpty();
    }

    @Test
    void save_shouldPersistTrainerWorkload() {
        repository.save(trainerWorkload);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isNotNull();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(trainerWorkload);
    }
}
