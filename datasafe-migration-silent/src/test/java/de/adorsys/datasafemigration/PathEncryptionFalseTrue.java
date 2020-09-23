package de.adorsys.datasafemigration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("filesystem-distinctfolder-pathencryption-true")
public class PathEncryptionFalseTrue extends  PathEncryptionBaseTest {
    @BeforeAll
    static public void setSystemProperty() { setOldPathEncryption(false); }

    @Test
    public void test() {
        expectCollectionsToBeEqual = false;
        doMigrationTest();
    }
}
