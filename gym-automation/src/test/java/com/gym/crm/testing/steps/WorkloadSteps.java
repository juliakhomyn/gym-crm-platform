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

import java.time.Duration;
import java.time.Month;
import java.util.Map;

import static com.gym.crm.testing.constants.ApiConstants.ACTION_TYPE;
import static com.gym.crm.testing.constants.ApiConstants.IS_ACTIVE;
import static com.gym.crm.testing.constants.ApiConstants.MONTH;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER_FIRST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER_LAST_NAME;
import static com.gym.crm.testing.constants.ApiConstants.TRAINER_USERNAME;
import static com.gym.crm.testing.constants.ApiConstants.TRAINING_DATE;
import static com.gym.crm.testing.constants.ApiConstants.TRAINING_DURATION;
import static com.gym.crm.testing.constants.ApiConstants.YEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class WorkloadSteps {
    private static final String WORKLOAD_URL = "/trainers/workload";
    public static final int DEFAULT_YEAR = 2026;
    public static final int DEFAULT_MONTH = 1;

    private final ApiClient client;
    private final TestContext context;

    public WorkloadSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.workloadUrl() + WORKLOAD_URL);
    }

    @Given("existing workload of {int} minutes in {string} {int}")
    public void existingWorkload(Integer duration, String month, Integer year) {
        String date = String.format("%d-%02d-15", year, Month.valueOf(month.toUpperCase()).getValue());

        Response response = client.post("", context.getToken(), buildWorkloadRequest("ADD", duration, date));
        context.setLastResponse(response);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @When("workload update is received:")
    public void workloadUpdateIsReceived(DataTable table) {
        Map<String, String> data = table.asMap();

        Response response = client.post("",
                context.getToken(),
                buildWorkloadRequest(data.get(ACTION_TYPE),
                        Integer.parseInt(data.get(TRAINING_DURATION)),
                        data.get(TRAINING_DATE)));

        context.setLastResponse(response);
    }

    @When("I request workload for user in {string} {int}")
    public void requestWorkload(String month, Integer year) {
        Response response = client.get("/" + trainer().getUsername(),
                context.getToken(),
                Map.of(YEAR, year, MONTH, Month.valueOf(month.toUpperCase()).getValue()));

        context.setLastResponse(response);
    }

    @When("I request workload without authentication")
    public void requestWithoutAuthentication() {
        Response response = client.get("/" + trainer().getUsername(),
                null,
                Map.of(YEAR, DEFAULT_YEAR, MONTH, DEFAULT_MONTH));

        context.setLastResponse(response);
    }

    @When("an empty workload update is received")
    public void emptyWorkloadUpdateIsReceived() {
        Response response = client.post("", context.getToken(), Map.of());
        context.setLastResponse(response);
    }

    @Then("trainer has {int} minutes in {string} {int}")
    public void trainerHasWorkload(Integer expectedDuration, String month, Integer year) {
        int monthValue = Month.valueOf(month.toUpperCase()).getValue();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    Response response = client.get("/" + trainer().getUsername(),
                            context.getToken(),
                            Map.of(YEAR, year, MONTH, monthValue));

                    assertThat(response.statusCode()).isEqualTo(200);
                    assertThat(response.jsonPath().getInt(TRAINING_DURATION))
                            .isEqualTo(expectedDuration);
                    assertThat(response.jsonPath().getInt(MONTH))
                            .isEqualTo(monthValue);
                    assertThat(response.jsonPath().getInt(YEAR))
                            .isEqualTo(year);});
    }

    private Map<String, Object> buildWorkloadRequest(String action, Integer duration, String date) {
        return Map.of(TRAINER_USERNAME, trainer().getUsername(),
                TRAINER_FIRST_NAME, trainer().getFirstName(),
                TRAINER_LAST_NAME, trainer().getLastName(),
                IS_ACTIVE, true,
                ACTION_TYPE, action,
                TRAINING_DURATION, duration,
                TRAINING_DATE, date);
    }

    private RegisteredUser trainer() {
        return context.getRegisteredUser(TRAINER);
    }
}