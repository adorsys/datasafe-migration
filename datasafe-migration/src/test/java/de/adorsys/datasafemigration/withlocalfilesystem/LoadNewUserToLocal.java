package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.S103_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_ListRecursiveFlag;
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
    private final S103_SimpleDatasafeService simpleDatasafeService;
    private final S103_DocumentDirectoryFQN dest;

    public void migrateUser(S103_UserIDAuth userIDAuth) {
        Security.addProvider(new BouncyCastleProvider());

        List<S103_DocumentFQN> list = simpleDatasafeService.list(userIDAuth, new S103_DocumentDirectoryFQN("/"), S103_ListRecursiveFlag.TRUE);
        for (S103_DocumentFQN fqn : list) {
            S103_DSDocument dsDocument = simpleDatasafeService.readDocument(userIDAuth, fqn);
            store(dsDocument, dest.addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(S103_DSDocument dsDocument, S103_DocumentDirectoryFQN dest) {
        log.debug("store {} bytes in local file {} from new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
