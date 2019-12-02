package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserIDAuth;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.S101_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocument;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_ListRecursiveFlag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j

public class LoadUserNewToNewFormat {
    private final S101_SimpleDatasafeService sourceDatasafeService;
    private final S101_SimpleDatasafeService destDatasafeService;

    public void migrateUser(S101_UserIDAuth userIDAuth) {

        createUser(userIDAuth);

        List<S101_DocumentFQN> list = sourceDatasafeService.list(userIDAuth, new S101_DocumentDirectoryFQN("/"), S101_ListRecursiveFlag.TRUE);
        for (S101_DocumentFQN fqn : list) {
            S101_DSDocument dsDocument = sourceDatasafeService.readDocument(userIDAuth, fqn);
            storeDocument(userIDAuth, dsDocument);
        }
    }

    private void createUser(S101_UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(S101_UserIDAuth userIDAuth, S101_DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }


}
