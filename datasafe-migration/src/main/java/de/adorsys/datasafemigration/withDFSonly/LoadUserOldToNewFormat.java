package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafemigration.common.SwitchVersion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@AllArgsConstructor
@Slf4j
public class LoadUserOldToNewFormat {
    private final de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService sourceDatasafeService;
    private final de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService destDatasafeService;

    public void migrateUser(de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth userIDAuth) {

        createUser(userIDAuth);

        List<de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentFQN> list = sourceDatasafeService.list(
                SwitchVersion.toOld(userIDAuth),
                new de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentDirectoryFQN("/"),
                de.adorsys.datasafe_0_6_1.simple.adapter.api.types.ListRecursiveFlag.TRUE);
        for (de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentFQN fqn : list) {
            de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DSDocument dsDocument = sourceDatasafeService.readDocument(SwitchVersion.toOld(userIDAuth), fqn);
            storeDocument(
                    userIDAuth,
                    SwitchVersion.toNew(dsDocument));
        }
    }

    private void createUser(de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth userIDAuth, de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }

}
