package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("minio")
public class MinioDirectDFSAccessTest extends DirectDFSAccessBaseTest {
    @Autowired
    SimpleDatasafeService datasafeService;

    @BeforeAll
    static void beforeAllHere() {
            minio().getStorageService().get();
            System.setProperty("MINIO_URL", minio().getMappedUrl());
    }

    @Test
    public void writeFilesToDFS() {
        super.testWriteFilesToDFS(datasafeService);
    }

    @Test
    public void testMoveFiles() {
        super.testMoveFiles(datasafeService);
    }

    @Test void testUserExists() {
        super.testUserExists(datasafeService);
    }
}
