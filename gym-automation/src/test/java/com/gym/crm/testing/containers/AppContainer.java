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
                .withEnv(commonProperties())
                .withEnv(coreProperties())
                .waitingFor(Wait.forHttp("/gym-crm/core/actuator/health")
                                .forStatusCode(200)
                                .withStartupTimeout(Duration.ofMinutes(3)));
    }

    public static GenericContainer<?> createWorkloadApp(Network network) {
        return new GenericContainer<>(DockerImageName.parse("workload-service:local"))
                .withNetwork(network)
                .withExposedPorts(8081)
                .withEnv(commonProperties())
                .withEnv(workloadProperties())
                .waitingFor(Wait.forHttp("/gym-crm/core/actuator/health")
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofMinutes(3)));
    }

    private static Map<String, String> commonProperties() {
        return Map.of("SPRING_PROFILES_ACTIVE", "local",
                "SPRING_ACTIVEMQ_BROKER_URL", "tcp://message-broker:61616",
                "SPRING_ACTIVEMQ_USER", "gymuser",
                "SPRING_ACTIVEMQ_PASSWORD", "gympass",
                "JWT_SECRET", "QWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo1Njc4OTAxMjM0NTY3OA==");
    }

    private static Map<String,String> coreProperties() {
        return Map.of("SPRING_PROFILES_ACTIVE", "test",
                "SPRING_DATASOURCE_URL", "jdbc:mysql://mysql-db:3306/gym_db",
                "SPRING_DATASOURCE_USERNAME", "gymuser",
                "SPRING_DATASOURCE_PASSWORD", "gympass",
                "SPRING_DATA_REDIS_HOST", "redis-cache",
                "EUREKA_CLIENT_ENABLED", "false",
                "JWT_SECRET", "testSecret");
    }

    private static Map<String,String> workloadProperties() {
        return Map.of("MONGODB_URI", "mongodb://localhost:27017/workload_db");
    }
}
