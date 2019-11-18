package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@ActiveProfiles("filesystem")
public class SilentMigrationFilesystemTest extends SilentMigrationBaseTest {

    @Autowired
    SimpleDatasafeService datasafeService;

    @BeforeAll
    static void beforeAllHere() {
        SimpleDatasafeServiceWithMigration.migrateToNewFolder = true;
    }

    @AfterAll
    static void afterAllHere() {
        SimpleDatasafeServiceWithMigration.migrateToNewFolder = false;
    }

    @Test
    public void testwithFilesystem() {
        basicTests(datasafeService);
    }

    @Test
    public void testMigrationwithFilesystem() {
        migrationTest(datasafeService, ((SimpleDatasafeServiceWithMigration) datasafeService).getCredentialsToNOTMigratedData());
    }

}
