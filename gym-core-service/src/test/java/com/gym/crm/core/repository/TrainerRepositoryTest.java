package com.gym.crm.core.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.core.model.Trainer;
import com.gym.crm.core.model.TrainingType;
import com.gym.crm.core.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/trainer.xml",
        cleanBefore = true,
        executeStatementsBefore = {
                "ALTER TABLE users ALTER COLUMN id RESTART WITH 100",
                "ALTER TABLE trainers ALTER COLUMN id RESTART WITH 100"
        })
class TrainerRepositoryTest extends AbstractRepositoryTest<TrainerRepository> {
    private static final String TRAINER_USERNAME = "Callum.Whitfield";
    private static final String TRAINEE_USERNAME = "Owen.Castleberry";
    private static final String TRAINER_PASSWORD = "$2a$12$t5uzrr.BRNAzZrEaP/Q5XuBrNaWG.wz277HZLUGI05fB8oUOx4p6a";
    private static final String FIRST_NAME = "Cillian";
    private static final String LAST_NAME = "Mercer";
    private static final String USERNAME = "Cillian.Mercer";
    private static final String PASSWORD = "password";
    private static final String SPECIALIZATION = "Yoga";
    private static final String UPDATED_FIRST_NAME = "New";
    private static final String UPDATED_LAST_NAME = "User";
    private static final String UPDATED_SPECIALIZATION = "Pilates";

    @Test
    void save_shouldSaveTrainerWithUser_whenValid() {
        Trainer trainer = buildTrainerToSave();

        Trainer actual = repository.save(trainer);

        assertThat(actual).isNotNull();
        assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo(SPECIALIZATION);
        assertThat(actual.getUser().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getUser().getLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(actual.getUser().getIsActive()).isTrue();

        Trainer found = repository.findByUserUsername(USERNAME).orElseThrow();

        assertThat(found).isNotNull();
        assertThat(found.getSpecialization().getTrainingTypeName()).isEqualTo(SPECIALIZATION);
        assertThat(found.getUser().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(found.getUser().getLastName()).isEqualTo(LAST_NAME);
        assertThat(found.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(found.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(found.getUser().getIsActive()).isTrue();
    }

    @Test
    void update_shouldUpdateFields_whenValid() {
        Trainer existing = repository.findByUserUsername(TRAINER_USERNAME).orElseThrow();
        User user = existing.getUser().toBuilder()
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .build();
        TrainingType trainingType = TrainingType.builder()
                .id(2L)
                .trainingTypeName(UPDATED_SPECIALIZATION)
                .build();
        Trainer trainer = existing.toBuilder()
                .specialization(trainingType)
                .user(user)
                .build();

        Trainer actual = repository.save(trainer);

        assertThat(actual).isNotNull();
        assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo(UPDATED_SPECIALIZATION);
        assertThat(actual.getUser().getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(actual.getUser().getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(actual.getUser().getUsername()).isEqualTo(TRAINER_USERNAME);
        assertThat(actual.getUser().getPassword()).isEqualTo(TRAINER_PASSWORD);
        assertThat(actual.getUser().getIsActive()).isTrue();
    }

    @Test
    void findByUserUsername_shouldReturnTrainer_whenExists() {
        Optional<Trainer> actual = repository.findByUserUsername(TRAINER_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(TRAINER_USERNAME);
    }

    @Test
    void findByUserUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = repository.findByUserUsername(TRAINEE_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsernameWithTrainees_shouldReturnTrainerWithTrainees_whenExists() {
        Optional<Trainer> actual = repository.findByUsernameWithTrainees(TRAINER_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(TRAINER_USERNAME);
        assertThat(actual.get().getTrainees())
                .extracting("user.username")
                .containsExactlyInAnyOrder(TRAINEE_USERNAME);
    }

    @Test
    void findByUsernameWithTrainees_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = repository.findByUsernameWithTrainees("Not.Found");

        assertThat(actual).isEmpty();
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnAllTrainers_whenNoneAssigned() {
        List<Trainer> actual = repository.findNotAssignedToTrainee("Petra.Dunmore");

        assertThat(actual)
                .extracting("user.username")
                .containsExactlyInAnyOrder(TRAINER_USERNAME, "Nora.Pemberton");
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnEmptyList_whenAllAssigned() {
        List<Trainer> actual = repository.findNotAssignedToTrainee(TRAINEE_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnUnassignedTrainers_whenSomeAssigned() {
        List<Trainer> actual = repository.findNotAssignedToTrainee("Ellis.Hargrove");

        assertThat(actual)
                .extracting(t -> t.getUser().getUsername())
                .containsExactlyInAnyOrder(TRAINER_USERNAME);
    }

    private Trainer buildTrainerToSave() {
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .build();
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(SPECIALIZATION)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(trainingType)
                .build();
    }
}
