package com.gym.crm.workload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WorkloadAppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void context_shouldLoadSuccessfully() {
        assertThat(context).isNotNull();
    }

    @Test
    void main_shouldRunWithoutExceptions() {
        WorkloadApp.main(new String[] {});
    }
}
