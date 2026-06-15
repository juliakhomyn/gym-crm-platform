package com.gym.crm.core.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.core.model.Trainee;
import com.gym.crm.core.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/trainee.xml",
        cleanBefore = true,
        executeStatementsBefore = {
            "ALTER TABLE users ALTER COLUMN id RESTART WITH 100",
            "ALTER TABLE trainees ALTER COLUMN id RESTART WITH 100"
})
class TraineeRepositoryTest extends AbstractRepositoryTest<TraineeRepository> {
    private static final String EXISTING_USERNAME = "Nora.Pemberton";
    private static final String EXISTING_PASSWORD = "$2a$12$EMDlDQnVEV.NCYG6OwO5yOSTV8LruKDXBHuhPYGzvTNTd2d7Py1lC";
    private static final String FIRST_NAME = "Cillian";
    private static final String LAST_NAME = "Mercer";
    private static final String USERNAME = "Cillian.Mercer";
    private static final String PASSWORD = "password";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private static final String ADDRESS = "123 Main St";
    private static final String UPDATED_FIRST_NAME = "New";
    private static final String UPDATED_LAST_NAME = "User";
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.of(2001, 1, 1);
    private static final String UPDATED_ADDRESS = "456 New St";

    @Test
    void save_shouldSaveTraineeWithUser_whenValid() {
        Trainee trainee = buildTraineeToSave();

        Trainee actual = repository.save(trainee);

        assertThat(actual).isNotNull();
        assertThat(actual.getDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
        assertThat(actual.getAddress()).isEqualTo(ADDRESS);
        assertThat(actual.getUser().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getUser().getLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(actual.getUser().getIsActive()).isTrue();

        Trainee found = repository.findById(actual.getId()).orElseThrow();

        assertThat(found.getDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
        assertThat(found.getAddress()).isEqualTo(ADDRESS);
        assertThat(found.getUser().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(found.getUser().getLastName()).isEqualTo(LAST_NAME);
        assertThat(found.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(found.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(found.getUser().getIsActive()).isTrue();
    }

    @Test
    void update_shouldUpdateFields_whenValid() {
        Trainee existing = repository.findByUserUsername(EXISTING_USERNAME).orElseThrow();
        User user = existing.getUser().toBuilder()
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .build();
        Trainee trainee = existing.toBuilder()
                .dateOfBirth(UPDATED_DATE_OF_BIRTH)
                .address(UPDATED_ADDRESS)
                .user(user)
                .build();

        Trainee actual = repository.save(trainee);

        assertThat(actual).isNotNull();
        assertThat(actual.getDateOfBirth()).isEqualTo(UPDATED_DATE_OF_BIRTH);
        assertThat(actual.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(actual.getUser().getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(actual.getUser().getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(actual.getUser().getUsername()).isEqualTo(EXISTING_USERNAME);
        assertThat(actual.getUser().getPassword()).isEqualTo(EXISTING_PASSWORD);
        assertThat(actual.getUser().getIsActive()).isTrue();
    }

    @Test
    void findByUsername_shouldReturnTrainee_whenExists() {
        Optional<Trainee> actual = repository.findByUserUsername(EXISTING_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(EXISTING_USERNAME);
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainee> actual = repository.findByUserUsername("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsernameWithTrainers_shouldReturnTraineeWithTrainers_whenExists() {
        Optional<Trainee> actual = repository.findByUsernameWithTrainers(EXISTING_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainers()).isNotNull();
    }

    private Trainee buildTraineeToSave() {
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .address(ADDRESS)
                .dateOfBirth(DATE_OF_BIRTH)
                .build();
    }
}
