package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Random;


@Slf4j
@ActiveProfiles("filesystem")
public class SilentMigrationTest extends InjectionTest {
    @Autowired
    SimpleDatasafeService datasafeService;
    private static int DOCUMENT_SIZE = 1000;

    @Test
    public void basicTests() {
        Assertions.assertNotNull(datasafeService);
        new ArrayList<Boolean>(100).forEach(el -> log.info("test is done"));
        log.info("Service injected: {}", SimpleDatasafeService.class.toString());

        UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("affe"::toCharArray));

        Assertions.assertFalse(datasafeService.userExists(userIDAuth.getUserID()));
        datasafeService.createUser(userIDAuth);
        Assertions.assertTrue(datasafeService.userExists(userIDAuth.getUserID()));

        DocumentFQN path = new DocumentFQN("peters/first/file.txt");
        DocumentContent content = createDocumentContent(DOCUMENT_SIZE);
        DSDocument dsDocument = new DSDocument(path, content);

        Assertions.assertFalse(datasafeService.documentExists(userIDAuth, path));
        datasafeService.storeDocument(userIDAuth, dsDocument);
        Assertions.assertTrue(datasafeService.documentExists(userIDAuth, path));

        DSDocument loadedDSDocument = datasafeService.readDocument(userIDAuth, path);
        Assertions.assertArrayEquals(loadedDSDocument.getDocumentContent().getValue(), content.getValue());

        datasafeService.deleteDocument(userIDAuth, path);
        Assertions.assertFalse(datasafeService.documentExists(userIDAuth, path));

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
