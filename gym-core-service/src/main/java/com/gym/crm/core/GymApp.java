package com.gym.crm.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class GymApp {

    public static void main( String[] args ) {
        SpringApplication.run(GymApp.class, args);
    }
}
