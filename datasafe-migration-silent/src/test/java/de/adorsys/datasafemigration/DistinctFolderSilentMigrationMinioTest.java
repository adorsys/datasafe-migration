package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@ActiveProfiles("minio-distinctfolder")
public class DistinctFolderSilentMigrationMinioTest extends DistinctSilentMigrationTest {

    @Autowired
    SimpleDatasafeService datasafeService;

    @BeforeAll
    static void beforeAllHere() {
        minio().getStorageService().get();
        System.setProperty("MINIO_URL",  minio().getMappedUrl());
    }

    @Test
    public void doBasicTest() {
        basicTests(datasafeService);
    }

    @Test
    public void doMigrationTest() {
        migrationTest(datasafeService);
    }

}
