package com.gym.crm.testing.steps;

import com.gym.crm.testing.client.ApiClient;
import com.gym.crm.testing.config.TestData;
import com.gym.crm.testing.containers.LocalTestEnvironment;
import com.gym.crm.testing.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {
    private static final String URL = "/auth";

    private final ApiClient client;
    private final TestContext context;

    public AuthSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.coreUrl() + URL);
    }

    @Given("a registered user with username {string} and password {string}")
    public void registeredUser(String username, String password) {
        context.put("username", username);
        context.put("password", password);
    }

    @Given("the user is authenticated")
    public void theUserIsAuthenticated() {
        login(TestData.TRAINER_USERNAME, TestData.TRAINER_PASSWORD);
    }

    @When("the user logs in with valid credentials")
    public void successfulLogin() {
        String username = context.get("username");
        String password = context.get("password");
        login(username, password);
    }

    @When("the user logs in with an incorrect password")
    public void loginWithWrongPassword() {
        login(TestData.TRAINEE_USERNAME, TestData.TRAINER_PASSWORD);
    }

    @When("the user logs in with username {string} and password {string}")
    public void loginWithUnknownUsername(String username, String password) {
        login(username, password);
    }

    @When("the user logs out")
    public void theUserLogsOut() {
        Map<String, Object> body = Map.of("username", TestData.TRAINER_USERNAME, "password", TestData.TRAINER_PASSWORD);
        Response response = client.post("/logout",
                context.getToken(),
                body);

        context.setLastResponse(response);
    }

    @When("the user accesses a protected endpoint with the same token")
    public void accessProtectedEndpoint() {
        Response response = client.get("/logout",
                context.getToken(),
                Map.of());

        context.setLastResponse(response);
    }

    @Then("the response status is {int}")
    public void responseStatus(int expected) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expected);
    }

    @Then("a JWT access token is returned")
    public void responseContainsJwtToken() {
        String token = context.getLastResponse()
                .jsonPath()
                .getString("token");

        assertThat(token).isNotBlank();
    }

    @Then("the response contains the authenticated username")
    public void responseContainsUsername() {
        assertThat(context.getLastResponse()
                .jsonPath()
                .getString("username"))
                .isEqualTo(TestData.TRAINER_USERNAME);
    }

    private void login(String username, String password) {
        Map<String, Object> body = Map.of("username", username, "password", password);
        Response response = client.post("/login", null, body);
        context.setLastResponse(response);

        if (response.statusCode() == 200) {
            context.setToken(response.jsonPath().getString("token"));
        }
    }
}
