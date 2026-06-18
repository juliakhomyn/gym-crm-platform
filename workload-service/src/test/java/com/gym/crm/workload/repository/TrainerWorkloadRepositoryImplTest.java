package com.gym.crm.workload.repository;

import com.gym.crm.workload.model.TrainerWorkload;
import com.gym.crm.workload.repository.impl.TrainerWorkloadRepositoryImpl;
import com.gym.crm.workload.utils.TestDataProvider;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerWorkloadRepositoryImplTest {
    private static final String USERNAME = "Callum.Whitfield";
    private static final String FIRST_NAME = "Callum";
    private static final String LAST_NAME = "Whitfield";

    private final TrainerWorkload workload = TestDataProvider.buildTrainerWorkload();
    private final TrainerWorkloadRepositoryImpl repository = new TrainerWorkloadRepositoryImpl();

    @Test
    void findByTrainerUsername_shouldReturnEmpty_whenWorkloadDoesNotExist() {
        Optional<TrainerWorkload> actual = repository.findByTrainerUsername("unknown");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnTrainerWorkload_whenExists() {
        repository.save(workload);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainerUsername()).isEqualTo(USERNAME);
    }

    @Test
    void save_shouldStoreWorkload() {
        repository.save(workload);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);
        assertThat(actual).isPresent();
        TrainerWorkload stored = actual.get();
        assertThat(stored.getTrainerUsername()).isEqualTo(USERNAME);
        assertThat(stored.getTrainerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(stored.getTrainerLastName()).isEqualTo(LAST_NAME);
        assertThat(stored.getIsActive()).isTrue();
        assertThat(stored.getYears()).isNotEmpty();
    }

    @Test
    void save_shouldOverwriteExistingWorkloadWithSameUsername() {
        TrainerWorkload updated = workload.toBuilder()
                .trainerFirstName("Updated")
                .build();

        repository.save(workload);
        repository.save(updated);

        Optional<TrainerWorkload> result = repository.findByTrainerUsername(USERNAME);

        assertThat(result).isPresent();
        assertThat(result.get().getTrainerFirstName()).contains("Updated");
        assertThat(result.get().getIsActive()).isTrue();
    }
}
