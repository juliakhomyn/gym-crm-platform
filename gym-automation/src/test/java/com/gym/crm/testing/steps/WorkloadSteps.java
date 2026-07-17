package com.gym.crm.testing.steps;

import com.gym.crm.testing.client.ApiClient;
import com.gym.crm.testing.config.TestData;
import com.gym.crm.testing.containers.LocalTestEnvironment;
import com.gym.crm.testing.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.Month;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkloadSteps {
    private static final String WORKLOAD_URL = "/trainers/workload";

    private final ApiClient client;
    private final TestContext context;

    public WorkloadSteps(TestContext context) {
        this.context = context;
        this.client = new ApiClient(LocalTestEnvironment.coreUrl() + WORKLOAD_URL);
    }

    @Given("existing workload of {int} minutes in {string} {int}")
    public void existingWorkload(Integer duration, String month, Integer year) {
        int monthValue = Month.valueOf(month.toUpperCase()).getValue();
        String formattedMonth = String.format("%02d", monthValue);
        String date = String.format("%d-%s-15", year, formattedMonth);

        Map<String, String> update = Map.of("action", "ADD",
                "duration", String.valueOf(duration),
                "date", date);

        Response response = client.post("", context.getToken(), update);
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @When("workload update is received:")
    public void workloadUpdateIsReceived(DataTable dataTable) {
        Map<String, String> update = dataTable.asMap();
        Response response = client.post("", context.getToken(), update);

        context.setLastResponse(response);
    }

    @When("I request workload for user in {string} {int}")
    public void requestWorkloadForUnknownTrainer(String month, Integer year) {
        Map<String, Object> body = Map.of("year", year, "month", Month.valueOf(month.toUpperCase()).getValue());

        Response response = client.get("/" + TestData.TRAINER_USERNAME,
                context.getToken(),
                body);

        context.setLastResponse(response);
    }

    @When("I request workload without authentication")
    public void requestWithoutAuthentication() {
        Map<String, Object> body = Map.of("year", TestData.YEAR, "month", TestData.MONTH);
        Response response = client.get("/" + TestData.TRAINER_USERNAME, null, body);

        context.setLastResponse(response);
    }

    @Then("trainer has {int} minutes in {string} {int}")
    public void trainerHasWorkload(Integer duration, String month, Integer year) {
        assertThat(context.getLastResponse()
                .jsonPath()
                .getInt("duration"))
                .isEqualTo(duration);
        assertThat(context.getLastResponse()
                .jsonPath()
                .getInt("month"))
                .isEqualTo(Month.valueOf(month.toUpperCase()).getValue());
        assertThat(context.getLastResponse()
                .jsonPath()
                .getInt("year"))
                .isEqualTo(year);
    }
}