package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@Slf4j
@ActiveProfiles("minio")
public class MinioDirectDFSAccessTest extends DirectDFSAccessBaseTest {
    @Autowired
    SimpleDatasafeService datasafeService;

    @BeforeAll
    static void beforeAllHere() {
        try {
            minio().getStorageService().get();
            System.setProperty("MINIO_URL", minio().getMappedUrl());
        } catch (Exception e) {
            log.error("exception during setup for minio", e);
            Arrays.stream(e.getStackTrace()).forEach(el -> log.error(el.toString()));
            throw e;
        }
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
