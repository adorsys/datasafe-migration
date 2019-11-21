package de.adorsys.datasafemigration;

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
        log.debug("beforeAll sets static member of SimpleDatasafeService");
        SimpleDatasafeServiceWithMigration.migrateToNewFolder = true;
    }

    @AfterAll
    static void afterAllHere() {
        SimpleDatasafeServiceWithMigration.migrateToNewFolder = false;
    }

    @Test
    public void doBasicTest() {
        basicTests(datasafeService);
    }

    @Test
    public void doMigrationTest() {
        migrationTest(datasafeService, ((SimpleDatasafeServiceWithMigration) datasafeService).getCredentialsToNOTMigratedData());
    }

}
