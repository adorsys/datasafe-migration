package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.ListRecursiveFlag;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafemigration.common.SwitchVersion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@AllArgsConstructor
@Slf4j
public class LoadUserOldToNewFormat {
    private final SimpleDatasafeService simpleDatasafeService;
    private final WriteUserNewFormat writeUserNewFormat;

    public void migrateUser(UserIDAuth userIDAuth) {

        writeUserNewFormat.createUser(userIDAuth);
        List<DocumentFQN> list = simpleDatasafeService.list(SwitchVersion.toOld(userIDAuth), new DocumentDirectoryFQN("/"), ListRecursiveFlag.TRUE);
        for (DocumentFQN fqn : list) {
            DSDocument dsDocument = simpleDatasafeService.readDocument(SwitchVersion.toOld(userIDAuth), fqn);
            writeUserNewFormat.storeDocument(
                    userIDAuth,
                    SwitchVersion.toNew(dsDocument));
        }
    }
}
