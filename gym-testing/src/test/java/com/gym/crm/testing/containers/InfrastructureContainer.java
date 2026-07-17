package com.gym.crm.testing.containers;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@NoArgsConstructor
public final class InfrastructureContainer {
    public static final String REDIS_HOST = "redis-cache";
    public static final String BROKER_HOST = "message-broker";

    public static GenericContainer<?> createRedis(Network network) {
        return new GenericContainer<>(DockerImageName.parse("redis:7.4.1"))
                .withNetwork(network)
                .withNetworkAliases(REDIS_HOST)
                .withExposedPorts(6379);
    }

    public static GenericContainer<?> createActiveMq(Network network) {
        return new GenericContainer<>(DockerImageName.parse("apache/activemq-classic:5.18.6"))
                .withNetwork(network)
                .withNetworkAliases(BROKER_HOST)
                .withExposedPorts(61616, 8161);
    }
}
