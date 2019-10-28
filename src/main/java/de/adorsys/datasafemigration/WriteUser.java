package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class WriteUser {

    @SneakyThrows
    public WriteUser(SimpleDatasafeService simpleDatasafeService, UserIDAuth userIDAuth, DocumentDirectoryFQN src) {

        if (simpleDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user "+userIDAuth.getUserID().getValue()+" already exists");
        }

        simpleDatasafeService.createUser(userIDAuth);

        String sourcedir = src.addDirectory(userIDAuth.getUserID().getValue()).getDocusafePath();
        try (Stream<Path> walk = Files.walk(Paths.get(sourcedir))) {
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            result.forEach(System.out::println);

            for (String path : result) {
                DocumentFQN fqn = new DocumentFQN(path.substring(sourcedir.length()));
                DSDocument dsDocument = new DSDocument(fqn, new DocumentContent(Files.readAllBytes(Paths.get(path))));
                simpleDatasafeService.storeDocument(userIDAuth, dsDocument);
                log.info("stored {}", dsDocument.getDocumentFQN().getDocusafePath());
            }
        }
    }
}
