package com.gym.crm.testing.client;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class ApiClient {

    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response get(String path, String token, Map<String, ?> queryParams) {
        RequestSpecification request = request(token).queryParams(queryParams);

        return request.get(baseUrl + path);
    }

    public Response post(String path, String token, Object body) {
        return request(token)
                .log().all()
                .body(body)
                .post(baseUrl + path);
    }

    private RequestSpecification request(String token) {
        RequestSpecification request = RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        if (token != null && !token.isBlank()) {
            request.header("Authorization", "Bearer " + token);
        }

        return request;
    }
}
