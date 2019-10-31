package de.adorsys.datasafemigration;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN;
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
    private final SimpleDatasafeService simpleDatasafeService;
    private final DocumentDirectoryFQN src;

    @SneakyThrows
    public void migrateUser(UserIDAuth userIDAuth) {

        if (simpleDatasafeService.userExists(SwitchVersion.toOld(userIDAuth).getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists in old format");
        }

        simpleDatasafeService.createUser(SwitchVersion.toOld(userIDAuth));
        log.info("create user {} in old format", userIDAuth.getUserID().getValue());

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            result.forEach(System.out::println);

            for (String path : result) {
                DocumentFQN fqn = new DocumentFQN(path.substring(sourcedir.length()));
                DSDocument dsDocument = new DSDocument(fqn, new DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(SwitchVersion.toOld(userIDAuth), dsDocument);
                log.info("stored {} in old format", dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
