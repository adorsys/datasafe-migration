package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("minio")
public class SilentMigrationMinioTest extends SilentMigrationBaseTest {

    @Autowired
    SimpleDatasafeService datasafeService;

    @BeforeAll
    static void startMinio() {
        minio().getStorageService().get();
        System.setProperty("MINIO_URL",  minio().getMappedUrl());
    }

    @Test
    public void testWithMinio() {
        basicTests(datasafeService);
    }

    @Test
    public void testMigrationwithFilesystem() {
        migrationTest(datasafeService, ((SimpleDatasafeServiceWithMigration) datasafeService).getCredentialsToNOTMigratedData());
    }

}
