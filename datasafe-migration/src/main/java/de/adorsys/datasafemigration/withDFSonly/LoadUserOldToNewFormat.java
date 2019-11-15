package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafemigration.common.SwitchVersion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@AllArgsConstructor
@Slf4j
public class LoadUserOldToNewFormat {
    private final S061_SimpleDatasafeService sourceDatasafeService;
    private final S100_SimpleDatasafeService destDatasafeService;

    public void migrateUser(S100_UserIDAuth userIDAuth) {

        createUser(userIDAuth);

        List<S061_DocumentFQN> list = sourceDatasafeService.list(SwitchVersion.to_0_6_1(userIDAuth), new S061_DocumentDirectoryFQN("/"), S061_ListRecursiveFlag.TRUE);
        for (S061_DocumentFQN fqn : list) {
            S061_DSDocument dsDocument = sourceDatasafeService.readDocument(SwitchVersion.to_0_6_1(userIDAuth), fqn);
            storeDocument(userIDAuth, SwitchVersion.to_1_0_0(dsDocument));
        }
    }

    private void createUser(S100_UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(S100_UserIDAuth userIDAuth, S100_DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }

}
