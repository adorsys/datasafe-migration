package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.S103_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentContent;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
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
    private final S103_SimpleDatasafeService simpleDatasafeService;
    private final S103_DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(S103_UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists in new format");
        }

        simpleDatasafeService.createUser(userIDAuth);
        log.debug("create user {} in new format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            for (String path : result) {
                S103_DocumentFQN fqn = new S103_DocumentFQN(path.substring(sourcedir.length()));
                S103_DSDocument dsDocument = new S103_DSDocument(fqn, new S103_DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(userIDAuth, dsDocument);
                log.debug("stored {} bytes for file {} in new format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
