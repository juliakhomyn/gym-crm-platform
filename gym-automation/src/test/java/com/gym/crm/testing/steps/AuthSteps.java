package com.gym.crm.testing.steps;

import com.gym.crm.testing.client.ApiClient;
import com.gym.crm.testing.context.RegisteredUser;
import com.gym.crm.testing.containers.LocalTestEnvironment;
import com.gym.crm.testing.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static com.gym.crm.testing.constants.ApiConstants.AUTH_URL;
import static com.gym.crm.testing.constants.ApiConstants.FIRST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.LAST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.LOGIN_URL;
import static com.gym.crm.testing.constants.ApiConstants.LOGOUT_URL;
import static com.gym.crm.testing.constants.ApiConstants.PASSWORD;
import static com.gym.crm.testing.constants.ApiConstants.REGISTER_TRAINEE_URL;
import static com.gym.crm.testing.constants.ApiConstants.REGISTER_TRAINER_URL;
import static com.gym.crm.testing.constants.ApiConstants.TOKEN;
import static com.gym.crm.testing.constants.ApiConstants.TRAINEE;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER;
import static com.gym.crm.testing.constants.ApiConstants.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {
    public static final String WRONG_PASSWORD = "password111";

    private final ApiClient client;
    private final TestContext context;

    public AuthSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.coreUrl());
    }

    @Given("a registered trainer with:")
    public void registeredTrainer(DataTable table) {
        RegisteredUser trainer = registerUser(REGISTER_TRAINER_URL, table.asMap());

        context.addRegisteredUser(TRAINER, trainer);
        context.setCurrentUser(trainer);
    }

    @Given("a registered trainee with:")
    public void registeredTrainee(DataTable table) {
        RegisteredUser trainee = registerUser(REGISTER_TRAINEE_URL, table.asMap());

        context.addRegisteredUser(TRAINEE, trainee);
    }

    @Given("the user is authenticated")
    public void theUserIsAuthenticated() {
        authenticateUser();
    }

    @Given("the trainee is authenticated")
    public void theTraineeIsAuthenticated() {
        context.setCurrentUser(context.getRegisteredUser(TRAINEE));
        authenticateUser();
    }

    @Given("the user is not authenticated")
    public void theUserIsNotAuthenticated() {
        context.setToken(null);
    }

    @When("the user logs in with valid credentials")
    public void successfulLogin() {
        authenticateUser();
    }

    @When("the user logs in with an incorrect password")
    public void loginWithWrongPassword() {
        login(context.getCurrentUser().getUsername(), WRONG_PASSWORD);
    }

    @When("the user logs in with username {string} and password {string}")
    public void loginWithUnknownUsername(String username, String password) {
        login(username, password);
    }

    @When("the user logs out")
    public void theUserLogsOut() {
        RegisteredUser user = context.getCurrentUser();

        Response response = client.post(AUTH_URL + LOGOUT_URL, context.getToken(), Map.of(USERNAME, user.getUsername(), PASSWORD, user.getPassword()));
        context.setLastResponse(response);
    }

    @When("the user accesses a protected endpoint with the same token")
    public void accessProtectedEndpoint() {
        Response response = client.get(AUTH_URL + LOGOUT_URL, context.getToken(), Map.of());
        context.setLastResponse(response);
    }

    @Then("the response status is {int}")
    public void responseStatus(int expected) {
        assertThat(context.getLastResponse().statusCode())
                .isEqualTo(expected);
    }

    @Then("a JWT access token is returned")
    public void responseContainsJwtToken() {
        assertThat(context.getLastResponse().jsonPath().getString(TOKEN))
                .isNotBlank();
    }

    @Then("the response contains the authenticated username")
    public void responseContainsUsername() {
        assertThat(context.getLastResponse().jsonPath().getString(USERNAME))
                .isEqualTo(context.getCurrentUser().getUsername());
    }

    private void login(String username, String password) {
        Response response = client.post(AUTH_URL + LOGIN_URL, null, Map.of(USERNAME, username, PASSWORD, password));
        context.setLastResponse(response);

        if (response.statusCode() == 200) {
            context.setToken(response.jsonPath().getString(TOKEN));
        }
    }

    private RegisteredUser registerUser(String url, Map<String, String> request) {
        Response response = client.post(url, null, request);
        context.setLastResponse(response);

        assertThat(response.statusCode()).isEqualTo(200);

        return RegisteredUser.builder()
                .username(response.jsonPath().getString(USERNAME))
                .password(response.jsonPath().getString(PASSWORD))
                .firstName(request.get(FIRST_NAME))
                .lastName(request.get(LAST_NAME))
                .build();
    }

    private void authenticateUser() {
        RegisteredUser user = context.getCurrentUser();
        login(user.getUsername(), user.getPassword());
    }
}