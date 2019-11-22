package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class DistinctSilentMigrationTest extends SilentMigrationBaseTest {
    private int numberOfAllDocsBefore = 0;
    private int numberOfUserDocsBefore = 0;

    @Override
    public void checkBeforeMigration(UserID userID, GetStorage.SystemRootAndStorageService service, int numberOfDocumentsOfUser) {
        log.debug("before migration of {}", userID.getValue());

        numberOfAllDocsBefore = DirectDFSAccess.listAllFiles(service).size();
        numberOfUserDocsBefore = findDocumentsOfUser(userID, service).size();

        // one keystore, one public keys
        Assertions.assertEquals(numberOfUserDocsBefore, numberOfDocumentsOfUser + 2);
    }


    @Override
    public void checkAfterMigration(UserID userID, GetStorage.SystemRootAndStorageService service, int numberOfDocumentsOfUser) {
        log.debug("after migration of {}", userID.getValue());
        int numberOfAllDocsAfter = DirectDFSAccess.listAllFiles(service).size();
        int numberOfUserDocsAfter  = findDocumentsOfUser(userID, service).size();
        int numberOfUserDocsAfterDistinct  = findDocumentsOfUserInNewLocation(userID, service).size();

        // one for new file with version
        Assertions.assertEquals(numberOfUserDocsBefore + 1, numberOfUserDocsAfterDistinct);
        Assertions.assertEquals(0, numberOfUserDocsAfter);
        Assertions.assertEquals(numberOfAllDocsBefore + 1, numberOfAllDocsAfter);
        log.info("all before {} all after {} user before {} user after {} users started {}", numberOfAllDocsBefore, numberOfAllDocsAfter, numberOfUserDocsBefore, numberOfUserDocsAfter, numberOfDocumentsOfUser);
    }
}
