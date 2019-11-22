package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("no-migration-filesystem")
public class NoMigrationFilesystemTest extends NoMigrationTest{
    @Autowired
    SimpleDatasafeService datasafeService;

    @Test
    public void doBasicTest() {
        basicTests(datasafeService);
    }

    @Test
    public void doMigrationTest() {
        migrationTest(datasafeService);
    }


}
