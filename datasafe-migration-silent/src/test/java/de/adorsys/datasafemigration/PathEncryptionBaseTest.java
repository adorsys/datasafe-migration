package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class PathEncryptionBaseTest extends WithMigrationTest {
    Collection<String> privateFilesBefore;
    Collection<String> privateFilesAfter;
    Boolean expectCollectionsToBeEqual = false;

    static public void setOldPathEncryption(Boolean onOrOff) {
        System.setProperty("SC-NO-BUCKETPATH-ENCRYPTION", onOrOff ? "false" : "true");
    }

    @Autowired
    SimpleDatasafeService datasafeService;

    public void doMigrationTest() {
        migrationTest(datasafeService);
    }

    @Override
    public void checkBeforeMigration(UserID userID, GetStorage.SystemRootAndStorageService service, int numberOfDocumentsOfUser) {
        privateFilesBefore = findDocumentsOfUser(userID, service)
            .stream().filter(doc -> doc.lastIndexOf("private/files") != -1)
            .map(doc -> doc.substring(doc.lastIndexOf("private/files")))
            .collect(Collectors.toList());
    }

    @Override
    public void checkAfterMigration(UserID userID, GetStorage.SystemRootAndStorageService service, int numberOfDocumentsOfUser) {
        privateFilesAfter = findDocumentsOfUserInNewLocation(userID, service)
            .stream().filter(doc -> doc.lastIndexOf("private/files") != -1)
            .map(doc -> doc.substring(doc.lastIndexOf("private/files")))
            .collect(Collectors.toList());

        Assertions.assertEquals(privateFilesAfter.size(), privateFilesBefore.size());
        Assertions.assertFalse(privateFilesAfter.isEmpty());
        if (expectCollectionsToBeEqual) {
            Assertions.assertEquals(privateFilesAfter, privateFilesBefore);
        } else {
            Assertions.assertFalse(privateFilesBefore.removeAll(privateFilesAfter));
            Assertions.assertFalse(privateFilesAfter.removeAll(privateFilesBefore));
        }
    }
}
