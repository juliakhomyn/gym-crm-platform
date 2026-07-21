package com.gym.crm.testing.context;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TestContext {
    private Response lastResponse;
    private String token;
    private RegisteredUser currentUser;

    private final Map<String, RegisteredUser> users = new HashMap<>();

    public void addRegisteredUser(String role, RegisteredUser user) {
        users.put(role, user);
    }

    public RegisteredUser getRegisteredUser(String role) {
        return users.get(role);
    }
}
