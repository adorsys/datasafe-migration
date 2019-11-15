package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.spring.annotations.UseDatasafeSpringConfiguration;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import de.adorsys.datasafemigration.docker.WithStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootConfiguration
@UseDatasafeSpringConfiguration
public class SilentMigrationBaseTest extends WithStorageProvider {
    private static int DOCUMENT_SIZE = 1000;

    public void basicTests(SimpleDatasafeService datasafeService) {
        log.info("DO ASSERT NOW");
        org.junit.jupiter.api.Assertions.assertNotNull(datasafeService);
        log.info("ASSERT OK");

        log.info("Service injected: {}", SimpleDatasafeService.class.toString());

        UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("affe"::toCharArray));

        org.junit.jupiter.api.Assertions.assertFalse(datasafeService.userExists(userIDAuth.getUserID()));
        datasafeService.createUser(userIDAuth);
        org.junit.jupiter.api.Assertions.assertTrue(datasafeService.userExists(userIDAuth.getUserID()));

        DocumentFQN path = new DocumentFQN("peters/first/file.txt");
        DocumentContent content = createDocumentContent(DOCUMENT_SIZE);
        DSDocument dsDocument = new DSDocument(path, content);

        org.junit.jupiter.api.Assertions.assertFalse(datasafeService.documentExists(userIDAuth, path));
        datasafeService.storeDocument(userIDAuth, dsDocument);
        org.junit.jupiter.api.Assertions.assertTrue(datasafeService.documentExists(userIDAuth, path));

        DSDocument loadedDSDocument = datasafeService.readDocument(userIDAuth, path);
        org.junit.jupiter.api.Assertions.assertArrayEquals(loadedDSDocument.getDocumentContent().getValue(), content.getValue());

        datasafeService.deleteDocument(userIDAuth, path);
        org.junit.jupiter.api.Assertions.assertFalse(datasafeService.documentExists(userIDAuth, path));

        datasafeService.destroyUser(userIDAuth);
        Assertions.assertFalse(datasafeService.userExists(userIDAuth.getUserID()));

        datasafeService.cleanupDb();
    }

    public static DocumentContent createDocumentContent(int sizeOfDocument) {
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);
        return new DocumentContent(bytes);
    }
}
