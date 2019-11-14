package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@ActiveProfiles("filesystem")
public class SilentMigrationFilesystemTest extends SilentMigrationBaseTest {

    @Autowired
    SimpleDatasafeService datasafeService;

    @Test
    public void testwithFilesystem() {
        basicTests(datasafeService);
    }
}
