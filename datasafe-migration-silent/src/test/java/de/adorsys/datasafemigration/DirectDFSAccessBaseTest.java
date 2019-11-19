package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import de.adorsys.datasafe.simple.adapter.spring.annotations.UseDatasafeSpringConfiguration;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafemigration.docker.WithStorageProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootConfiguration
@UseDatasafeSpringConfiguration
public class DirectDFSAccessBaseTest extends WithStorageProvider {

    public void writeFilesToDFS(SimpleDatasafeService datasafeService) {
        S100_DFSCredentials s100_dfsCredentials = ((SimpleDatasafeServiceWithMigration) datasafeService).getCredentialsToMigratedData();
        GetStorage.SystemRootAndStorageService systemRootAndStorageService = GetStorage.get(s100_dfsCredentials);
        DSDocument dsDocument = new DSDocument(new DocumentFQN("filename.txt"), new DocumentContent("content of file".getBytes()));
        UserID userID = new UserID("peter");
        DirectDFSAccess.storeFileInUsersRootDir(systemRootAndStorageService, userID, dsDocument);
    }

    @SneakyThrows
    public void testMoveFiles(SimpleDatasafeService datasafeService) {
        S100_DFSCredentials s100_dfsCredentials = ((SimpleDatasafeServiceWithMigration) datasafeService).getCredentialsToMigratedData();
        GetStorage.SystemRootAndStorageService base = GetStorage.get(s100_dfsCredentials);

        GetStorage.SystemRootAndStorageService source = new GetStorage.SystemRootAndStorageService(new URI(base.getSystemRoot().toString() + ("source")), base.getStorageService());
        GetStorage.SystemRootAndStorageService dest = new GetStorage.SystemRootAndStorageService(new URI(base.getSystemRoot().toString() + ("dest")), base.getStorageService());
        UserID userID = new UserID("peter");
        Set<DocumentFQN> filenames = new HashSet<>();
        createNames(filenames, new DocumentDirectoryFQN("/"), 5, 10, 3);
        for (DocumentFQN fqn : filenames) {
            DSDocument dsDocument = new DSDocument(fqn, createDocumentContent("content of file " + fqn.getDocusafePath() + ".", 10000));
            DirectDFSAccess.storeFileInUsersRootDir(source, userID, dsDocument);
        }
        log.info(DirectDFSAccess.moveAllFiles(source, dest, userID).toString());
    }

    private void createNames(Set<DocumentFQN> result, DocumentDirectoryFQN dir, int numberOfFilesPerFolder, int numberOfDirsPerFolder, int depthOfFolders) {
        if (depthOfFolders == 0) {
            return;
        }
        for (int i = 0; i < numberOfFilesPerFolder; i++) {
            result.add(ExtendedSwitchVersion.toCurrent(dir.addName("file" + i)));
        }
        for (int i = 0; i < numberOfDirsPerFolder; i++) {
            createNames(result, dir.addDirectory("dir" + i), numberOfFilesPerFolder, numberOfDirsPerFolder, depthOfFolders-1);
        }
    }


    public static DocumentContent createDocumentContent(String seed, int sizeOfDocument) {
        if (sizeOfDocument < seed.length()) {
            sizeOfDocument = seed.length();
        }
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);
        System.arraycopy(seed.getBytes(), 0, bytes, 0, seed.length());
        return new DocumentContent(bytes);
    }

}
