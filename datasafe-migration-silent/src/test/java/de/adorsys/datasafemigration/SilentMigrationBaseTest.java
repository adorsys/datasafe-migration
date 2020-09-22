package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import de.adorsys.datasafe.simple.adapter.spring.annotations.UseDatasafeSpringConfiguration;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.S061_SimpleDatasafeServiceImpl;
import de.adorsys.datasafemigration.docker.WithStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@UseDatasafeSpringConfiguration
@DirtiesContext(classMode=AFTER_EACH_TEST_METHOD)
abstract public class SilentMigrationBaseTest extends WithStorageProvider {

    public abstract void checkBeforeMigration(UserID userID, GetStorage.SystemRootAndStorageService service, int numberOfDocumentsOfUser);
    public abstract void checkAfterMigration(UserID userID, GetStorage.SystemRootAndStorageService service, int numberOfDocumentsOfUser);
    public abstract void checkAfterLastMigration(SimpleDatasafeService simpleDatasafeService, Map<S061_UserIDAuth, Set<S061_DSDocument>> oldStructure);

    private static int DOCUMENT_SIZE = 1000;

    protected void basicTests(SimpleDatasafeService datasafeService) {
        Assertions.assertNotNull(datasafeService);
        log.debug("Service successfully injected: {}", SimpleDatasafeService.class.toString());
        datasafeService.cleanupDb();

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

    protected void migrationTest(SimpleDatasafeService simpleDatasafeService) {
        log.info("START MIGRATION TEST");

        S061_DFSCredentials credentialsToNOTMigratedData = ((SimpleDatasafeServiceWithMigration) simpleDatasafeService).getCredentialsToNOTMigratedData();
        S061_SimpleDatasafeService s061_simpleDatasafeService = new S061_SimpleDatasafeServiceImpl(credentialsToNOTMigratedData);
        s061_simpleDatasafeService.cleanupDb();

        Set<S061_UserIDAuth> s061_userIDAuths = CreateStructureUtil.getS061_userIDAuths();
        Map<S061_UserIDAuth, Set<S061_DSDocument>> structure = CreateStructureUtil.create061Structure(s061_simpleDatasafeService, s061_userIDAuths);


        for(S061_UserIDAuth oldUser : s061_userIDAuths) {
            GetStorage.SystemRootAndStorageService systemRootAndStorageService = GetStorage.get(ExtendedSwitchVersion.to_1_0_3(credentialsToNOTMigratedData));
            UserID userID = ExtendedSwitchVersion.toCurrent(ExtendedSwitchVersion.to_1_0_3(oldUser)).getUserID();
            checkBeforeMigration(userID, systemRootAndStorageService, structure.get(oldUser).size());
            simpleDatasafeService.readDocument(
                    ExtendedSwitchVersion.toCurrent(ExtendedSwitchVersion.to_1_0_3(oldUser)),
                    ExtendedSwitchVersion.toCurrent(ExtendedSwitchVersion.to_1_0_3(structure.get(oldUser).stream().findFirst().get().getDocumentFQN())));
            checkAfterMigration(userID, systemRootAndStorageService, structure.get(oldUser).size());
        }

        checkAfterLastMigration(simpleDatasafeService, structure);


        simpleDatasafeService.cleanupDb();
    }

    private static DocumentContent createDocumentContent(int sizeOfDocument) {
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);
        return new DocumentContent(bytes);
    }

    protected List<String> findDocumentsOfUser(UserID userID, GetStorage.SystemRootAndStorageService service) {
        String base = service.getSystemRoot().toASCIIString();
        return DirectDFSAccess.listAllFiles(service).stream().sorted().filter(el -> el.contains(base + "users/" + userID.getValue() + "/")).collect(Collectors.toList());
    }

    protected List<String> findDocumentsOfUserInNewLocation(UserID userID, GetStorage.SystemRootAndStorageService service) {
        String base = service.getSystemRoot().toASCIIString();
        return DirectDFSAccess.listAllFiles(service).stream().sorted().filter(el -> el.contains(base + "100/users/" + userID.getValue() + "/")).collect(Collectors.toList());
    }

}
