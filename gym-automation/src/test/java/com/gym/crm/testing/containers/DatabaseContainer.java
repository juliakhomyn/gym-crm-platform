package com.gym.crm.testing.containers;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@NoArgsConstructor
public final class DatabaseContainer {
    public static final String MYSQL_HOST = "mysql-db";
    public static final String MONGO_HOST = "mongo-db";

    public static MySQLContainer<?> createMysql(Network network) {
        return new MySQLContainer<>(DockerImageName.parse("mysql:8.4.3"))
                .withDatabaseName("gym_db")
                .withUsername("gymuser")
                .withPassword("gympass")
                .withNetwork(network)
                .withNetworkAliases(MYSQL_HOST);
    }

    public static MongoDBContainer createMongo(Network network) {
        return new MongoDBContainer(DockerImageName.parse("mongo:7.0.5"))
                .withNetwork(network)
                .withNetworkAliases(MONGO_HOST);
    }
}
