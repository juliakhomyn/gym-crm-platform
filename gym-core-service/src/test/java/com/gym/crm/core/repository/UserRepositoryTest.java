package com.gym.crm.core.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.core.model.User;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/user.xml", cleanBefore = true)
class UserRepositoryTest extends AbstractRepositoryTest<UserRepository> {
    private static final String USERNAME = "Simone.Radcliffe";

    @Test
    void save_shouldSaveUser_whenValid() {
        User user = TestDataProvider.buildTrainerUser();

        User actual = repository.save(user);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUsername()).isEqualTo("Owen.Castleberry");
        assertThat(actual.getFirstName()).isEqualTo("Owen");
        assertThat(actual.getLastName()).isEqualTo("Castleberry");
        assertThat(actual.getIsActive()).isTrue();
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User expected = TestDataProvider.buildTraineeUser();

        Optional<User> actual = repository.findById(1L);

        assertThat(actual).isPresent()
                .contains(expected);
        assertThat(actual.get().getUsername()).isEqualTo(expected.getUsername());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<User> actual = repository.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUser_whenExists() {
        User expected = TestDataProvider.buildTraineeUser();

        Optional<User> actual = repository.findByUsername(USERNAME);

        assertThat(actual).isPresent()
                .contains(expected);
        assertThat(actual.get().getUsername()).isEqualTo(expected.getUsername());
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<User> actual = repository.findByUsername("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        boolean actual = repository.existsByUsername(USERNAME);

        assertThat(actual).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        boolean result = repository.existsByUsername("non.existent");

        assertThat(result).isFalse();
    }

    @Test
    void findAll_shouldReturnAllUsers_whenExist() {
        User user1 = TestDataProvider.buildTraineeUser();
        User user2 = TestDataProvider.buildUser("Nora", "Pemberton", "Nora.Pemberton");
        User user3 = TestDataProvider.buildUser("Ellis", "Hargrove", "Ellis.Hargrove");

        List<User> actual = repository.findAll();

        assertThat(actual)
                .hasSize(3)
                .contains(user1, user2, user3);
    }
}
