package com.gym.crm.testing.context;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestContext {
    private Response lastResponse;
    private String token;
    private RegisteredUser registeredUser;
}
