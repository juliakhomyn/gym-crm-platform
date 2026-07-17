package com.gym.crm.testing.containers;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;

@NoArgsConstructor
public final class LocalTestEnvironment {
    private static Network network;
    private static MySQLContainer<?> mysql;
    private static MongoDBContainer mongo;
    private static GenericContainer<?> redis;
    private static GenericContainer<?> activemq;
    private static GenericContainer<?> core;
    private static GenericContainer<?> workload;

    public static void start() {
        network = Network.newNetwork();
        mysql = DatabaseContainer.createMysql(network);
        mongo = DatabaseContainer.createMongo(network);
        redis = InfrastructureContainer.createRedis(network);
        activemq = InfrastructureContainer.createActiveMq(network);

        mysql.start();
        mongo.start();
        redis.start();
        activemq.start();

        core = AppContainer.createCoreApp(network);
        workload = AppContainer.createWorkloadApp(network);
        core.start();
        workload.start();
    }

    public static void stop() {
        stop(workload);
        stop(core);
        stop(activemq);
        stop(redis);
        stop(mysql);
        stop(mongo);

        if(network != null) {
            network.close();
        }
    }

    public static String coreUrl() {
        return "http://" + core.getHost() + ":" + core.getMappedPort(8082) + "/gym-crm/core/api/v1";
    }

    public static String workloadUrl() {
        return "http://" + workload.getHost() + ":" + workload.getMappedPort(8081) + "/gym-crm/workload/api/v1";
    }

    private static void stop(GenericContainer<?> container) {
        if(container != null) {
            container.stop();
        }
    }
}
