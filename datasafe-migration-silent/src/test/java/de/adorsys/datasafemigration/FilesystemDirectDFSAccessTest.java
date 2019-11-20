package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("filesystem")
public class FilesystemDirectDFSAccessTest extends DirectDFSAccessBaseTest {
    @Autowired
    SimpleDatasafeService datasafeService;

    @Test
    public void writeFilesToDFS() {
        super.testWriteFilesToDFS(datasafeService);
    }

    @Test
    public void testMoveFiles() {
        super.testMoveFiles(datasafeService);
    }
}
