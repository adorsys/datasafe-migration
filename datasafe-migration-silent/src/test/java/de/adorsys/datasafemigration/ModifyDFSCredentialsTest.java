package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DFSCredentials;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_FilesystemDFSCredentials;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModifyDFSCredentialsTest {
    @Test
    public void testBestCase() {
        S101_DFSCredentials newRoot = ModifyDFSCredentials.getPathToMigratedData(getDFSCredentials("s3://adorsys-timp-dev/datasafe/backend/"));
        Assertions.assertEquals("s3://adorsys-timp-dev/datasafe/100/backend/", getRoot(newRoot));
    }

    @Test
    public void testBestCase2() {
        S101_DFSCredentials newRoot = ModifyDFSCredentials.getPathToMigratedData(getDFSCredentials("datasafe/backend"));
        Assertions.assertEquals("datasafe/100/backend/", getRoot(newRoot));
    }

    @Test
    public void testDefaultCase() {
        S101_DFSCredentials newRoot = ModifyDFSCredentials.getPathToMigratedData(getDFSCredentials("anyOtherRoot"));
        Assertions.assertEquals("anyOtherRoot/100/", getRoot(newRoot));
    }

    @Test
    public void testAppend() {
        S101_DFSCredentials newRoot = ModifyDFSCredentials.appendToRootPath(getDFSCredentials("anyOtherRoot"), "affe");
        Assertions.assertEquals("anyOtherRoot/affe/", getRoot(newRoot));
    }

    private S101_DFSCredentials getDFSCredentials(String rootPath) {
        return S101_FilesystemDFSCredentials.builder().root(rootPath).build();
    }

    private String getRoot(S101_DFSCredentials dfsCredentials) {
        return ModifyDFSCredentials.getCurrentRootPath(dfsCredentials);
    }
}
