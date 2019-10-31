package de.adorsys.datasafemigration;

import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.ListRecursiveFlag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class LoadNewUserToLocal {
    private final SimpleDatasafeService simpleDatasafeService;
    private final DocumentDirectoryFQN dest;

    public void migrateUser(UserIDAuth userIDAuth) {
        Security.addProvider(new BouncyCastleProvider());

        List<DocumentFQN> list = simpleDatasafeService.list(userIDAuth, new DocumentDirectoryFQN("/"), ListRecursiveFlag.TRUE);
        for (DocumentFQN fqn : list) {
            DSDocument dsDocument = simpleDatasafeService.readDocument(userIDAuth, fqn);
            log.info("loaded {} in new format", dsDocument.getDocumentFQN().getDocusafePath());
            store(dsDocument, dest.addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(DSDocument dsDocument, DocumentDirectoryFQN dest) {
        log.info("store local file {}", dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
