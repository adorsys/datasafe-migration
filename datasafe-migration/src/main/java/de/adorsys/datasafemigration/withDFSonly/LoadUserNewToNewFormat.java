package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.S103_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_ListRecursiveFlag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j

public class LoadUserNewToNewFormat {
    private final S103_SimpleDatasafeService sourceDatasafeService;
    private final S103_SimpleDatasafeService destDatasafeService;

    public void migrateUser(S103_UserIDAuth userIDAuth) {

        createUser(userIDAuth);

        List<S103_DocumentFQN> list = sourceDatasafeService.list(userIDAuth, new S103_DocumentDirectoryFQN("/"), S103_ListRecursiveFlag.TRUE);
        for (S103_DocumentFQN fqn : list) {
            S103_DSDocument dsDocument = sourceDatasafeService.readDocument(userIDAuth, fqn);
            storeDocument(userIDAuth, dsDocument);
        }
    }

    private void createUser(S103_UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(S103_UserIDAuth userIDAuth, S103_DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }


}
