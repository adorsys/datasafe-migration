package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserIDAuth;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.S101_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocument;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_ListRecursiveFlag;
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
    private final S101_SimpleDatasafeService simpleDatasafeService;
    private final S101_DocumentDirectoryFQN dest;

    public void migrateUser(S101_UserIDAuth userIDAuth) {
        Security.addProvider(new BouncyCastleProvider());

        List<S101_DocumentFQN> list = simpleDatasafeService.list(userIDAuth, new S101_DocumentDirectoryFQN("/"), S101_ListRecursiveFlag.TRUE);
        for (S101_DocumentFQN fqn : list) {
            S101_DSDocument dsDocument = simpleDatasafeService.readDocument(userIDAuth, fqn);
            store(dsDocument, dest.addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(S101_DSDocument dsDocument, S101_DocumentDirectoryFQN dest) {
        log.debug("store {} bytes in local file {} from new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
