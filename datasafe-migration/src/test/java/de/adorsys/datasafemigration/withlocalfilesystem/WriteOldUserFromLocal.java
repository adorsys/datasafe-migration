package de.adorsys.datasafemigration.withlocalfilesystem;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentContent;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
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
    private final S061_SimpleDatasafeService simpleDatasafeService;
    private final S100_DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(S100_UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(SwitchVersion.to_0_6_1(userIDAuth).getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists in old format");
        }

        simpleDatasafeService.createUser(SwitchVersion.to_0_6_1(userIDAuth));
        log.debug("create user {} in old format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            for (String path : result) {
                S061_DocumentFQN fqn = new S061_DocumentFQN(path.substring(sourcedir.length()));
                S061_DSDocument dsDocument = new S061_DSDocument(fqn, new S061_DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(SwitchVersion.to_0_6_1(userIDAuth), dsDocument);
                log.debug("stored {} bytes for file {} in old format", dsDocument.getDocumentContent().getValue().length, dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
