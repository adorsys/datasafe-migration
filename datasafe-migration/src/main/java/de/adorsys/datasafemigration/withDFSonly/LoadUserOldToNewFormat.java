package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.SO_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafemigration.common.SwitchVersion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@AllArgsConstructor
@Slf4j
public class LoadUserOldToNewFormat {
    private final SO_SimpleDatasafeService sourceDatasafeService;
    private final SimpleDatasafeService destDatasafeService;

    public void migrateUser(de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth userIDAuth) {

        createUser(userIDAuth);

        List<SO_DocumentFQN> list = sourceDatasafeService.list(SwitchVersion.toOld(userIDAuth), new SO_DocumentDirectoryFQN("/"), SO_ListRecursiveFlag.TRUE);
        for (SO_DocumentFQN fqn : list) {
            SO_DSDocument dsDocument = sourceDatasafeService.readDocument(SwitchVersion.toOld(userIDAuth), fqn);
            storeDocument(userIDAuth, SwitchVersion.toNew(dsDocument));
        }
    }

    private void createUser(de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth userIDAuth, de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }

}
