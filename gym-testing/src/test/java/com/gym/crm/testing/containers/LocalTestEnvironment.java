package com.gym.crm.testing.containers;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;

@NoArgsConstructor
public final class LocalTestEnvironment {
    private static Network network;
    private static MySQLContainer<?> mysql;
    private static GenericContainer<?> redis;
    private static GenericContainer<?> activemq;
    private static GenericContainer<?> core;

    public static void start() {
        network = Network.newNetwork();
        mysql = DatabaseContainer.createMysql(network);
        redis = InfrastructureContainer.createRedis(network);
        activemq = InfrastructureContainer.createActiveMq(network);

        mysql.start();
        redis.start();
        activemq.start();

        core = AppContainer.createCoreApp(network);
        core.start();
    }

    public static void stop() {
        stop(core);
        stop(activemq);
        stop(redis);
        stop(mysql);

        if(network != null) {
            network.close();
        }
    }

    public static String coreUrl() {
        return "http://" + core.getHost() + ":" + core.getMappedPort(8082) + "/gym-crm/core/api/v1";
    }

    private static void stop(GenericContainer<?> container) {
        if(container != null) {
            container.stop();
        }
    }
}
