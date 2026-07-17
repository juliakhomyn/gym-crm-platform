package com.gym.crm.testing.containers;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;

@NoArgsConstructor
public final class AppContainer {

    public static GenericContainer<?> createCoreApp(Network network) {
        return new GenericContainer<>(DockerImageName.parse("gym-core-service:local"))
                .withNetwork(network)
                .withExposedPorts(8082)
                .withEnv(coreProperties())
                .waitingFor(Wait.forHttp("/gym-crm/core/actuator/health")
                                .forStatusCode(200)
                                .withStartupTimeout(Duration.ofMinutes(3)));
    }

    private static Map<String,String> coreProperties() {
        return Map.of("SPRING_PROFILES_ACTIVE", "test",
                "SPRING_DATASOURCE_URL", "jdbc:mysql://mysql-db:3306/gym_db",
                "SPRING_DATASOURCE_USERNAME", "gymuser",
                "SPRING_DATASOURCE_PASSWORD", "gympass",
                "SPRING_DATA_REDIS_HOST", "redis-cache",
                "SPRING_ACTIVEMQ_BROKER_URL", "tcp://message-broker:61616",
                "EUREKA_CLIENT_ENABLED", "false",
                "JWT_SECRET", "testSecret"
        );
    }
}
