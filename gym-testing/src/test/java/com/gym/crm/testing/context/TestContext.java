package com.gym.crm.testing.context;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestContext {
    private Response lastResponse;
    private String token;
    private final Map<String, Object> objects = new HashMap<>();

    public void put(String key, Object value) {
        objects.put(key, value);
    }

    public String get(String key) {
        return (String) objects.get(key);
    }
}
