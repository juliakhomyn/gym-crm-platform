package com.gym.crm.core.service.common;

import com.gym.crm.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {
    private static final String FIRST_NAME = "Cillian";
    private static final String LAST_NAME = "Mercer";
    private static final String USERNAME = "Cillian.Mercer";
    private static final String USERNAME_WITH_SUFFIX_1 = "Cillian.Mercer1";
    private static final String USERNAME_WITH_SUFFIX_2 = "Cillian.Mercer2";
    
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UsernameGenerator generator;

    @Test
    void generateUsername_shouldReturnConcatenatedUsername_whenNoDuplicates() {
        when(repository.existsByUsername(USERNAME)).thenReturn(false);

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME);
    }

    @Test
    void generateUsername_shouldReturnUsernameWithSuffix_whenDuplicateExists() {
        when(repository.existsByUsername(USERNAME)).thenReturn(true);
        when(repository.existsByUsername(USERNAME_WITH_SUFFIX_1)).thenReturn(false);

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME_WITH_SUFFIX_1);
    }

    @Test
    void generateUsername_shouldReturnUsernameWithSuffix2_whenTwoDuplicatesExist() {
        when(repository.existsByUsername(USERNAME)).thenReturn(true);
        when(repository.existsByUsername(USERNAME_WITH_SUFFIX_1)).thenReturn(true);
        when(repository.existsByUsername(USERNAME_WITH_SUFFIX_2)).thenReturn(false);

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME_WITH_SUFFIX_2);
    }
}
