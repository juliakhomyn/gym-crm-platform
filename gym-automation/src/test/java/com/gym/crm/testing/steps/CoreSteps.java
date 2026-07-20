package com.gym.crm.testing.steps;

import com.gym.crm.testing.client.ApiClient;
import com.gym.crm.testing.containers.LocalTestEnvironment;
import com.gym.crm.testing.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static com.gym.crm.testing.constants.ApiConstants.FIRST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.LAST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.REGISTER_TRAINEE_URL;
import static com.gym.crm.testing.constants.ApiConstants.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

public class CoreSteps {
    private final ApiClient client;
    private final TestContext context;

    public CoreSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.coreUrl());
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
}
