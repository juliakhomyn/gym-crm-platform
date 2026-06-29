package com.gym.crm.core.messaging;

import com.gym.crm.core.model.Training;
import com.gym.crm.core.utils.TestDataProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainerWorkloadMapperTest {

    private final Training training = TestDataProvider.buildTraining();

    private final TrainerWorkloadMapper mapper = new TrainerWorkloadMapper();

    @Test
    void toMessage_shouldMapAllFieldsCorrectly() {
        TrainerWorkloadMessage message = mapper.toMessage(training, ActionType.ADD);

        assertThat(message.getTrainerUsername()).isEqualTo("Owen.Castleberry");
        assertThat(message.getTrainerFirstName()).isEqualTo("Owen");
        assertThat(message.getTrainerLastName()).isEqualTo("Castleberry");
        assertThat(message.getIsActive()).isTrue();
        assertThat(message.getTrainingDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(message.getTrainingDuration()).isEqualTo(60);
        assertThat(message.getActionType()).isEqualTo(ActionType.ADD);
    }

    @Test
    void toMessage_shouldSetActionTypeCorrectly() {
        TrainerWorkloadMessage message = mapper.toMessage(training, ActionType.DELETE);

        assertThat(message.getActionType()).isEqualTo(ActionType.DELETE);
    }
}
