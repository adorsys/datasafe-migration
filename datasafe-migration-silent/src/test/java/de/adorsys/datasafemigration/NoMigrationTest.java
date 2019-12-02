package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NoMigrationTest extends SilentMigrationBaseTest {
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
        Assertions.assertEquals(0, numberOfUserDocsAfterDistinct);

        // one for new file with version for user
        Assertions.assertEquals(numberOfUserDocsBefore, numberOfUserDocsAfter);
        Assertions.assertEquals(numberOfAllDocsBefore, numberOfAllDocsAfter);
        log.info("all before {} all after {} user before {} user after {} users started {}", numberOfAllDocsBefore, numberOfAllDocsAfter, numberOfUserDocsBefore, numberOfUserDocsAfter, numberOfDocumentsOfUser);

    }

    @Override
    public void checkAfterLastMigration(SimpleDatasafeService simpleDatasafeService, Map<S061_UserIDAuth, Set<S061_DSDocument>> oldStructure) {
        // Now all data must be readable with new service

        for(S061_UserIDAuth oldUser: oldStructure.keySet()) {
            UserIDAuth newUser = ExtendedSwitchVersion.toCurrent(ExtendedSwitchVersion.to_1_0_1(oldUser));
            Set<S061_DSDocument> oldDocsOfUser = oldStructure.get(oldUser);

            List<DocumentFQN> list = simpleDatasafeService.list(newUser, new DocumentDirectoryFQN("/"), ListRecursiveFlag.TRUE);
            Assertions.assertEquals(oldDocsOfUser.size(), list.size());
            for (S061_DSDocument oldDoc : oldDocsOfUser) {
                DSDocument oldDocument = ExtendedSwitchVersion.toCurrent(ExtendedSwitchVersion.to_1_0_1(oldDoc));
                Assertions.assertTrue(list.contains(oldDocument.getDocumentFQN()));
                DSDocument newDocument = simpleDatasafeService.readDocument(newUser, oldDocument.getDocumentFQN());
                Assertions.assertArrayEquals(oldDocument.getDocumentContent().getValue(), newDocument.getDocumentContent().getValue());
                list.remove(oldDocument.getDocumentFQN());
            }
            Assertions.assertTrue(list.isEmpty());
            log.info("successfully compared all {} old documents to all new documents of user {}", oldDocsOfUser.size(), oldUser.getUserID().getValue());
        }
    }
}
