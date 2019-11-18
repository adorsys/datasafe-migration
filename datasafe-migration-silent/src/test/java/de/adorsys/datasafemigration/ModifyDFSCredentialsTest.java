package de.adorsys.datasafemigration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModifyDFSCredentialsTest {
    @Test
    public void testBestCase() {
        String bestcase =   "s3://adorsys-timp-dev/datasafe/backend/";
        String newRoot = ModifyDFSCredentials.getNewRootPath(bestcase);
        Assertions.assertEquals("s3://adorsys-timp-dev/datasafe/100/backend/", newRoot);
    }

    @Test
    public void testBestCase2() {
        String bestcase =   "datasafe/backend";
        String newRoot = ModifyDFSCredentials.getNewRootPath(bestcase);
        Assertions.assertEquals("datasafe/100/backend/", newRoot);
    }

    @Test
    public void testDefaultCase() {
        String bestcase =   "anyOtherRoot";
        String newRoot = ModifyDFSCredentials.getNewRootPath(bestcase);
        Assertions.assertEquals("anyOtherRoot/100/", newRoot);
    }
}
