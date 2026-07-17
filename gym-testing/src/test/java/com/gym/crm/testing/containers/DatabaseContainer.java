package com.gym.crm.testing.containers;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@NoArgsConstructor
public final class DatabaseContainer {
    public static final String MYSQL_HOST = "mysql-db";

    public static MySQLContainer<?> createMysql(Network network) {
        return new MySQLContainer<>(DockerImageName.parse("mysql:8.4.3"))
                .withDatabaseName("gym_db")
                .withUsername("gymuser")
                .withPassword("gympass")
                .withNetwork(network)
                .withNetworkAliases(MYSQL_HOST);
    }
}
