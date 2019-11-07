package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.ListRecursiveFlag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j

public class LoadUserNewToNewFormat {
    private final SimpleDatasafeService sourceDatasafeService;
    private final SimpleDatasafeService destDatasafeService;

    public void migrateUser(UserIDAuth userIDAuth) {

        createUser(userIDAuth);

        List<DocumentFQN> list = sourceDatasafeService.list(
                userIDAuth,
                new DocumentDirectoryFQN("/"),
                ListRecursiveFlag.TRUE);
        for (DocumentFQN fqn : list) {
            DSDocument dsDocument = sourceDatasafeService.readDocument(userIDAuth, fqn);
            storeDocument(
                    userIDAuth,
                    dsDocument);
        }
    }

    private void createUser(UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(UserIDAuth userIDAuth, DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }


}
