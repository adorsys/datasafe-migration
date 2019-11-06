package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_0_7_0.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentFQN;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class WriteNewUserFromLocal {
    private final SimpleDatasafeService simpleDatasafeService;
    private final DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists in new format");
        }

        simpleDatasafeService.createUser(userIDAuth);
        log.debug("create user {} in new format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            for (String path : result) {
                DocumentFQN fqn = new DocumentFQN(path.substring(sourcedir.length()));
                DSDocument dsDocument = new DSDocument(fqn, new DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(userIDAuth, dsDocument);
                log.debug("stored {} bytes for file {} in new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
