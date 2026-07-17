package com.gym.crm.testing.steps;

import com.gym.crm.testing.client.ApiClient;
import com.gym.crm.testing.containers.LocalTestEnvironment;
import com.gym.crm.testing.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CoreSteps {
    private final static String TRAINEE_URL = "/trainees";

    private final ApiClient client;
    private final TestContext context;

    public CoreSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.coreUrl() + TRAINEE_URL);
    }

    @When("the client registers a trainee with:")
    public void registerTrainee(DataTable table) {
        Map<String, String> trainee = table.asMap();

        Response response = client.post("/register", null, trainee);
        context.setLastResponse(response);
    }

    @Then("the response contains generated username")
    public void responseContainsGeneratedUsername() {
        String username = context.getLastResponse()
                .jsonPath()
                .getString("username");

        assertThat(username).isNotBlank();
    }

    @Then("trainee first name is {string}")
    public void verifyFirstName(String firstName) {
        assertThat(context.getLastResponse()
                        .jsonPath()
                        .getString("firstName"))
                .isEqualTo(firstName);
    }

    @Then("trainee last name is {string}")
    public void verifyLastName(String lastName) {
        assertThat(context.getLastResponse()
                        .jsonPath()
                        .getString("lastName"))
                .isEqualTo(lastName);
    }
}
