package com.gym.crm.core.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DBRider
@DBUnit(cacheConnection = false, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected T repository;
}
