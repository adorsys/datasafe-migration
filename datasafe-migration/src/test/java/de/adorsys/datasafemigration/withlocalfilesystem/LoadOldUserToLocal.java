package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafemigration.common.SwitchVersion;
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
public class LoadOldUserToLocal {
    private final S061_SimpleDatasafeService simpleDatasafeService;
    private final S100_DocumentDirectoryFQN dest;

    public void migrateUser(S100_UserIDAuth userIDAuth) {
        Security.addProvider(new BouncyCastleProvider());

        List<S061_DocumentFQN> list = simpleDatasafeService.list(SwitchVersion.to_0_6_1(userIDAuth), new S061_DocumentDirectoryFQN("/"), S061_ListRecursiveFlag.TRUE);
        for (S061_DocumentFQN fqn : list) {
            S061_DSDocument dsDocument = simpleDatasafeService.readDocument(SwitchVersion.to_0_6_1(userIDAuth), fqn);
            store(dsDocument, SwitchVersion.to_0_6_1(dest).addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(S061_DSDocument dsDocument, S061_DocumentDirectoryFQN dest) {
        log.debug("store {} bytes in local file {} from old format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
