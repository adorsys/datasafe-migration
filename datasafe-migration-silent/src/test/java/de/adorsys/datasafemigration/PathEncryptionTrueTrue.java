package de.adorsys.datasafemigration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("filesystem-distinctfolder-pathencryption-true")
public class PathEncryptionTrueTrue extends  PathEncryptionBaseTest {
    @BeforeAll
    static public void setSystemProperty() { setOldPathEncryption(true); }

    @Test
    public void test() {
        // both encrypted - but with different algorithms, so name will not match
        expectCollectionsToBeEqual = false;
        doMigrationTest();
    }
}
