package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.SO_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentFQN;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafemigration.common.SwitchVersion;
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
public class WriteOldUserFromLocal {
    private final SO_SimpleDatasafeService simpleDatasafeService;
    private final DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(SwitchVersion.toOld(userIDAuth).getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists in old format");
        }

        simpleDatasafeService.createUser(SwitchVersion.toOld(userIDAuth));
        log.debug("create user {} in old format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            for (String path : result) {
                SO_DocumentFQN fqn = new SO_DocumentFQN(path.substring(sourcedir.length()));
                SO_DSDocument dsDocument = new SO_DSDocument(fqn, new DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(SwitchVersion.toOld(userIDAuth), dsDocument);
                log.debug("stored {} bytes for file {} in old format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
