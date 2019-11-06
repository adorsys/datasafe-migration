package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_0_7_0.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.ListRecursiveFlag;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LoadUserNewToNewFormat {
    private final SimpleDatasafeService simpleDatasafeService;
    private final WriteUserNewFormat writeUserNewFormat;

    public void migrateUser(UserIDAuth userIDAuth) {
        writeUserNewFormat.createUser(userIDAuth);
        List<DocumentFQN> list = simpleDatasafeService.list(userIDAuth, new DocumentDirectoryFQN("/"), ListRecursiveFlag.TRUE);
        for (DocumentFQN fqn : list) {
            DSDocument dsDocument = simpleDatasafeService.readDocument(userIDAuth, fqn);
            writeUserNewFormat.storeDocument(
                    userIDAuth,
                    dsDocument);
        }
    }
}
