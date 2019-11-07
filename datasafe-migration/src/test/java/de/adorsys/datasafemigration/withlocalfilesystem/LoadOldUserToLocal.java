package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.SO_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_ListRecursiveFlag;
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
    private final SO_SimpleDatasafeService simpleDatasafeService;
    private final de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN dest;

    public void migrateUser(de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth userIDAuth) {
        Security.addProvider(new BouncyCastleProvider());

        List<SO_DocumentFQN> list = simpleDatasafeService.list(SwitchVersion.toOld(userIDAuth), new SO_DocumentDirectoryFQN("/"), SO_ListRecursiveFlag.TRUE);
        for (SO_DocumentFQN fqn : list) {
            SO_DSDocument dsDocument = simpleDatasafeService.readDocument(SwitchVersion.toOld(userIDAuth), fqn);
            store(dsDocument, SwitchVersion.toOld(dest).addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(SO_DSDocument dsDocument, SO_DocumentDirectoryFQN dest) {
        log.debug("store {} bytes in local file {} from old format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
