package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("filesystem")
public class SilentMigrationFilesystemTest extends NotDistinctSilentMigrationTest {

    @Autowired
    SimpleDatasafeService datasafeService;

    @Test
    public void doBasicTest() {
        basicTests(datasafeService);
    }

    @Test
    public void doMigrationTest() {
        migrationTest(datasafeService, ((SimpleDatasafeServiceWithMigration) datasafeService).getCredentialsToNOTMigratedData());
    }


}
