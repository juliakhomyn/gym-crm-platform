package com.gym.crm.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class GatewayAppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void context_shouldLoadSuccessfully() {
        assertThat(context).isNotNull();
    }

    @Test
    void main_shouldRunWithoutExceptions() {
        assertDoesNotThrow(() -> GatewayApp.main(new String[] {}));
    }
}
