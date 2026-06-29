package com.gym.crm.core.facade;

import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.gym.crm.core.messaging.TrainerWorkloadMessageSender;
import com.gym.crm.core.search.filter.TraineeTrainingFilter;
import com.gym.crm.core.search.filter.TrainerTrainingFilter;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DBRider
@DBUnit(cacheConnection = false, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
@DataSet(value = "/dataset/training.xml", cleanBefore = true,
        executeStatementsBefore = {
                "ALTER TABLE users ALTER COLUMN id RESTART WITH 100",
                "ALTER TABLE trainees ALTER COLUMN id RESTART WITH 100"
        })
@ActiveProfiles("test")
class AuthorizationIntegrationTest {
    private static final String TRAINEE_USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String NOT_AUTHORIZED_USERNAME = "Nora.Pemberton";

    @MockBean
    private TrainerWorkloadMessageSender sender;

    @Autowired
    private GymFacade gymFacade;

    @Test
    void createTrainee_shouldReturnResponse() {
        TraineeCreateRequest request = TestDataProvider.buildTraineeCreateRequest();

        TraineeCreateResponse actual = gymFacade.createTrainee(request);

        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isNotNull();
        assertThat(actual.getPassword()).isNotNull();
    }

    @Test
    void getTraineeByUsername_shouldReturnResponse() {
        TraineeGetResponse actual = gymFacade.getTraineeByUsername(TRAINEE_USERNAME);

        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo("Simone");
        assertThat(actual.getLastName()).isEqualTo("Radcliffe");
    }

    @Test
    void getTrainerByUsername_shouldReturnResponse() {
        TrainerGetResponse actual = gymFacade.getTrainerByUsername(TRAINER_USERNAME);

        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo("Owen");
        assertThat(actual.getLastName()).isEqualTo("Castleberry");
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME)
    void deleteTraineeByUsername_shouldSucceed_whenUsernameMatchesPrincipal() {
        assertDoesNotThrow(() -> gymFacade.deleteTraineeByUsername(TRAINEE_USERNAME));
    }

    @Test
    @WithMockUser(username = NOT_AUTHORIZED_USERNAME)
    void deleteTraineeByUsername_shouldFail_whenUsernameDoesNotMatchPrincipal() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> gymFacade.deleteTraineeByUsername(TRAINEE_USERNAME));

        assertThat(exception.getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME)
    void getTrainersNotAssignedToTrainee_shouldSucceed_whenUsernameMatchesPrincipal() {
        List<AssignedTrainerResponse> actual = gymFacade.getTrainersNotAssignedToTrainee(TRAINEE_USERNAME);

        assertThat(actual).isNotNull();
    }

    @Test
    @WithMockUser(username = NOT_AUTHORIZED_USERNAME)
    void getTrainersNotAssignedToTrainee_shouldFail_whenUsernameDoesNotMatchPrincipal() {
        assertThrows(AccessDeniedException.class, () -> gymFacade.getTrainersNotAssignedToTrainee(TRAINEE_USERNAME));
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME)
    void changePassword_shouldSucceed_whenUsernameMatchesPrincipal() {
        LoginChangeRequest request = TestDataProvider.buildLoginChangeRequest();

        assertDoesNotThrow(() -> gymFacade.changePassword(request));
    }

    @Test
    @WithMockUser(username = NOT_AUTHORIZED_USERNAME)
    void changePassword_shouldFail_whenUsernameDoesNotMatchPrincipal() {
        LoginChangeRequest request = TestDataProvider.buildLoginChangeRequest();

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> gymFacade.changePassword(request));

        assertThat(exception.getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME)
    void deleteTraining_shouldFail_whenUsernameDoesNotMatchPrincipal() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> gymFacade.deleteTraining(1L, TRAINER_USERNAME));

        assertThat(exception.getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME)
    void getTraineeTrainingsByFilter_shouldSucceed_whenUsernameMatchesPrincipal() {
        TraineeTrainingFilter filter = TestDataProvider.buildTraineeTrainingFilter();

        List<GetTraineeTrainingResponse> actual = gymFacade.getTraineeTrainingsByFilter(filter);

        assertThat(actual).isNotNull();
    }

    @Test
    @WithMockUser(username = NOT_AUTHORIZED_USERNAME)
    void getTraineeTrainingsByFilter_shouldFail_whenUsernameDoesNotMatchPrincipal() {
        TraineeTrainingFilter filter = TestDataProvider.buildTraineeTrainingFilter();

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> gymFacade.getTraineeTrainingsByFilter(filter));

        assertThat(exception.getMessage()).contains("Access Denied");
    }

    @Test
    @WithMockUser(username = TRAINER_USERNAME)
    void getTrainerTrainingsByFilter_shouldSucceed_whenUsernameMatchesPrincipal() {
        TrainerTrainingFilter filter = TestDataProvider.buildTrainerTrainingFilter();

        List<GetTrainerTrainingResponse> actual = gymFacade.getTrainerTrainingsByFilter(filter);

        assertThat(actual).isNotNull();
    }

    @Test
    @WithMockUser(username = NOT_AUTHORIZED_USERNAME)
    void getTrainerTrainingsByFilter_shouldFail_whenUsernameDoesNotMatchPrincipal() {
        TrainerTrainingFilter filter = TestDataProvider.buildTrainerTrainingFilter();

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> gymFacade.getTrainerTrainingsByFilter(filter));

        assertThat(exception.getMessage()).contains("Access Denied");
    }
}
