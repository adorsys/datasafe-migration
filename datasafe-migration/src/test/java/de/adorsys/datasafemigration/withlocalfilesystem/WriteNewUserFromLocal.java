package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserIDAuth;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.S101_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocument;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentContent;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentFQN;
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
    private final S101_SimpleDatasafeService simpleDatasafeService;
    private final S101_DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(S101_UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists in new format");
        }

        simpleDatasafeService.createUser(userIDAuth);
        log.debug("create user {} in new format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            for (String path : result) {
                S101_DocumentFQN fqn = new S101_DocumentFQN(path.substring(sourcedir.length()));
                S101_DSDocument dsDocument = new S101_DSDocument(fqn, new S101_DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(userIDAuth, dsDocument);
                log.debug("stored {} bytes for file {} in new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
