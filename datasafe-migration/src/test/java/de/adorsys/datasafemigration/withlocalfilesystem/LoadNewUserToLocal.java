package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_ListRecursiveFlag;
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
    private final S100_SimpleDatasafeService simpleDatasafeService;
    private final S100_DocumentDirectoryFQN dest;

    public void migrateUser(S100_UserIDAuth userIDAuth) {
        Security.addProvider(new BouncyCastleProvider());

        List<S100_DocumentFQN> list = simpleDatasafeService.list(userIDAuth, new S100_DocumentDirectoryFQN("/"), S100_ListRecursiveFlag.TRUE);
        for (S100_DocumentFQN fqn : list) {
            S100_DSDocument dsDocument = simpleDatasafeService.readDocument(userIDAuth, fqn);
            store(dsDocument, dest.addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(S100_DSDocument dsDocument, S100_DocumentDirectoryFQN dest) {
        log.debug("store {} bytes in local file {} from new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
