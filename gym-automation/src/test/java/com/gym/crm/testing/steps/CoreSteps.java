package com.gym.crm.testing.steps;

import com.gym.crm.testing.client.ApiClient;
import com.gym.crm.testing.containers.LocalTestEnvironment;
import com.gym.crm.testing.context.RegisteredUser;
import com.gym.crm.testing.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static com.gym.crm.testing.constants.ApiConstants.ADD_TRAINING_URL;
import static com.gym.crm.testing.constants.ApiConstants.DELETE_TRAINEE_URL;
import static com.gym.crm.testing.constants.ApiConstants.FIRST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.LAST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.REGISTER_TRAINEE_URL;
import static com.gym.crm.testing.constants.ApiConstants.TRAINEE;
import static com.gym.crm.testing.constants.ApiConstants.TRAINEE_USERNAME;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER_USERNAME;
import static com.gym.crm.testing.constants.ApiConstants.TRAINING_DATE;
import static com.gym.crm.testing.constants.ApiConstants.TRAINING_DURATION;
import static com.gym.crm.testing.constants.ApiConstants.TRAINING_NAME;
import static com.gym.crm.testing.constants.ApiConstants.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

public class CoreSteps {
    private static final String DEFAULT_TRAINING_NAME = "Yoga";
    private static final String DEFAULT_TRAINING_DATE = "2026-01-15";
    private static final String DEFAULT_TRAINING_DURATION = "60";

    private final ApiClient client;
    private final TestContext context;

    public CoreSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.coreUrl());
    }

    @Given("the trainee has training sessions")
    public void traineeHasTrainingSession() {
        createTraining(buildTraining(DEFAULT_TRAINING_NAME, DEFAULT_TRAINING_DATE, DEFAULT_TRAINING_DURATION));
    }

    @When("the client registers a user with:")
    public void registerTrainee(DataTable table) {
        Map<String, String> trainee = table.asMap();

        Response response = client.post(REGISTER_TRAINEE_URL, null, trainee);
        context.setLastResponse(response);
    }

    @When("empty registration request is received")
    public void emptyRegistrationRequestReceived() {
        Response response = client.post(REGISTER_TRAINEE_URL, null, Map.of());
        context.setLastResponse(response);
    }

    @When("the client creates a training with:")
    public void createTrainingWithData(DataTable table) {
        createTraining(table.asMap());
    }

    @When("trainee is deleted")
    public void traineeIsDeleted() {
        Response response = client.delete(DELETE_TRAINEE_URL + "/" + trainee().getUsername(), context.getToken());
        context.setLastResponse(response);
    }

    @Then("the response contains generated username")
    public void responseContainsGeneratedUsername() {
        String username = context.getLastResponse()
                .jsonPath()
                .getString(USERNAME);

        assertThat(username).isNotBlank();
    }

    @Then("trainee first name is {string}")
    public void verifyFirstName(String firstName) {
        assertThat(context.getLastResponse()
                        .jsonPath()
                        .getString(FIRST_NAME))
                .isEqualTo(firstName);
    }

    @Then("trainee last name is {string}")
    public void verifyLastName(String lastName) {
        assertThat(context.getLastResponse()
                        .jsonPath()
                        .getString(LAST_NAME))
                .isEqualTo(lastName);
    }

    private void createTraining(Map<String, String> training) {
        Response response = client.post(ADD_TRAINING_URL,
                context.getToken(),
                buildTraining(training.get(TRAINING_NAME),
                        training.get(TRAINING_DATE),
                        training.get(TRAINING_DURATION)));

        context.setLastResponse(response);
    }

    private Map<String, String> buildTraining(String name, String date, String duration) {
        return Map.of(TRAINEE_USERNAME, trainee().getUsername(),
                TRAINER_USERNAME, trainer().getUsername(),
                TRAINING_NAME, name,
                TRAINING_DATE, date,
                TRAINING_DURATION, duration);
    }

    private RegisteredUser trainer() {
        return context.getRegisteredUser(TRAINER);
    }

    private RegisteredUser trainee() {
        return context.getRegisteredUser(TRAINEE);
    }
}
