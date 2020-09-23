package de.adorsys.datasafemigration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("filesystem-distinctfolder-pathencryption-false")
public class PathEncryptionTrueFalse extends  PathEncryptionBaseTest {
    @BeforeAll
    static public void setSystemProperty() { setOldPathEncryption(true); }

    @Test
    public void test() {
        expectCollectionsToBeEqual = false;
        doMigrationTest();
    }
}
