package com.gym.crm.testing.containers;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

public class TestLifecycle {

    @BeforeAll
    public static void prepare() {
        LocalTestEnvironment.start();
    }

    @AfterAll
    public static void cleanup() {
        LocalTestEnvironment.stop();
    }
}
