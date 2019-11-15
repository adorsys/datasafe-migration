package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentContent;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;
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
    private final S100_SimpleDatasafeService simpleDatasafeService;
    private final S100_DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(S100_UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists in new format");
        }

        simpleDatasafeService.createUser(userIDAuth);
        log.debug("create user {} in new format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            for (String path : result) {
                S100_DocumentFQN fqn = new S100_DocumentFQN(path.substring(sourcedir.length()));
                S100_DSDocument dsDocument = new S100_DSDocument(fqn, new S100_DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(userIDAuth, dsDocument);
                log.debug("stored {} bytes for file {} in new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
