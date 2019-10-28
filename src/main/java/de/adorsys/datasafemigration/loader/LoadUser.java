package de.adorsys.datasafemigration.loader;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.List;

@Slf4j
public class LoadUser {
    public LoadUser(SimpleDatasafeService simpleDatasafeService, UserIDAuth userIDAuth, DocumentDirectoryFQN dest) {
        Security.addProvider(new BouncyCastleProvider());

        List<DocumentFQN> list = simpleDatasafeService.list(userIDAuth, new DocumentDirectoryFQN("/"), ListRecursiveFlag.TRUE);
        for (DocumentFQN fqn : list) {
            DSDocument dsDocument = simpleDatasafeService.readDocument(userIDAuth, fqn);
            store(dsDocument, dest.addDirectory(userIDAuth.getUserID().getValue()));
        }
    }

    @SneakyThrows
    private void store(DSDocument dsDocument, DocumentDirectoryFQN dest) {
        log.info("store file {}", dsDocument.getDocumentFQN().getDatasafePath());
        Path localFileToWrite = Paths.get(dest.addDirectory(dsDocument.getDocumentFQN().getDocusafePath()).getDocusafePath());
        Files.createDirectories(localFileToWrite.getParent());
        Files.write(localFileToWrite, dsDocument.getDocumentContent().getValue());
    }
}
