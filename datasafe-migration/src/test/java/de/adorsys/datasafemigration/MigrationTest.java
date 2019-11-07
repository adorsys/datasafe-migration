package de.adorsys.datasafemigration;


import de.adorsys.datasafe_0_6_1.simple.adapter.api.SO_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.SO_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserID;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.FilesystemDFSCredentials;
import de.adorsys.datasafe_0_7_1.simple.adapter.impl.SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_0_7_1.types.api.types.ReadKeyPassword;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import de.adorsys.datasafemigration.withlocalfilesystem.LoadNewUserToLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteOldUserFromLocal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MigrationTest {

    private Path tempDir;

    @SneakyThrows
    @BeforeEach
    public void init() {
        tempDir = Files.createTempDirectory("migration-test");
    }

    @Test
    public void testMigrationWithLocalFiles() {

        // create tree of files for one user on local disk (below tempDir)
        UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("password"::toCharArray));
        DocumentDirectoryFQN startDatadir = new DocumentDirectoryFQN(tempDir.toString()).addDirectory("startupfiles");
        createLocalFilesInFolder(startDatadir.addDirectory(userIDAuth.getUserID().getValue()), 3, 3, 2, 1000);

        // move file tree of user to old datasafe format. destination depending on dataservice config
        SO_SimpleDatasafeService oldService = createOldService(tempDir.toString() + "/0.6.1");
        WriteOldUserFromLocal oldWriter = new WriteOldUserFromLocal(oldService, startDatadir);
        oldWriter.migrateUser(userIDAuth);

        // migrate old filetree to new filetree
        SimpleDatasafeService newService = createNewService(tempDir.toString() + "/0.7.1");
        LoadUserOldToNewFormat migrator = new LoadUserOldToNewFormat(oldService, newService);
        migrator.migrateUser(userIDAuth);

        // load all data from new filetree to local disk
        DocumentDirectoryFQN destDatadir = new DocumentDirectoryFQN(tempDir.toString()).addDirectory("loadedfromnewfiles");
        LoadNewUserToLocal loadNewUserToLocal = new LoadNewUserToLocal(newService, destDatadir);
        loadNewUserToLocal.migrateUser(userIDAuth);

        // compare initial tree to reloaded tree on local disk
        compare(startDatadir, destDatadir);
    }

    @Test
    public void testIncompatibility() {

        // create tree of files for one user on local disk (below tempDir)
        UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("password"::toCharArray));
        DocumentDirectoryFQN startDatadir = new DocumentDirectoryFQN(tempDir.toString()).addDirectory("startupfiles");
        createLocalFilesInFolder(startDatadir.addDirectory(userIDAuth.getUserID().getValue()), 1, 1, 0, 1000);

        // move file tree of user to old datasafe format. destination depending on dataservice config
        SO_SimpleDatasafeService oldService = createOldService(tempDir.toString() + "/0.6.1");
        WriteOldUserFromLocal oldWriter = new WriteOldUserFromLocal(oldService, startDatadir);
        oldWriter.migrateUser(userIDAuth);

        // try to read old files with new datasafe
        SimpleDatasafeService newDestService = createNewService(tempDir.toString() + "/0.7.1");
        Assertions.assertThrows(IOException.class, () -> newDestService.list(
                userIDAuth,
                new de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN("/"),
                de.adorsys.datasafe_0_7_1.simple.adapter.api.types.ListRecursiveFlag.TRUE));

    }

    @SneakyThrows
    private void compare(DocumentDirectoryFQN srcFolder, DocumentDirectoryFQN destFolder) {
        List<String> srcList;
        {
            try (Stream<Path> srcWalk = Files.walk(Paths.get(srcFolder.getDocusafePath()))) {
                srcList = srcWalk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());
            }
        }

        List<String> destList;
        {
            try (Stream<Path> srcWalk = Files.walk(Paths.get(destFolder.getDocusafePath()))) {
                destList = srcWalk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());
            }
        }

        List<String> pathOnlyListFromSrc = new ArrayList<>();
        srcList.forEach(el -> pathOnlyListFromSrc.add(el.substring(srcFolder.getDocusafePath().length() + 1)));

        List<String> pathOnlyListFromDest = new ArrayList<>();
        destList.forEach(el -> pathOnlyListFromDest.add(el.substring(destFolder.getDocusafePath().length() + 1)));

        Assertions.assertEquals(pathOnlyListFromSrc, pathOnlyListFromDest);

        int counter = 0;
        for (String el : pathOnlyListFromSrc) {
            DocumentFQN srcFQN = srcFolder.addName(el);
            DocumentFQN destFQN = destFolder.addName(el);

            byte[] srcBytes = Files.readAllBytes(Paths.get(srcFQN.getDocusafePath()));
            byte[] destBytes = Files.readAllBytes(Paths.get(destFQN.getDocusafePath()));

            Assertions.assertArrayEquals(srcBytes, destBytes);
            counter++;
        }
        ;

        log.info("all {} files have same content :-)", counter);
    }



    @SneakyThrows
    private static void createLocalFilesInFolder(DocumentDirectoryFQN path, int recursiveDepth, int numberOfFiles, int numberOfSubdirs, int sizeOfFile) {
        if (recursiveDepth == 0) {
            return;
        }

        for (int i = 0; i < numberOfFiles; i++) {
            Path localFileToWrite = Paths.get(path.addName("File_" + i).getDocusafePath());
            log.debug("create file " + localFileToWrite);
            Files.createDirectories(localFileToWrite.getParent());
            Files.write(localFileToWrite, createDocumentContent(sizeOfFile));
        }

        for (int i = 0; i < numberOfSubdirs; i++) {
            createLocalFilesInFolder(path.addDirectory("SUBDIR_" + i), recursiveDepth - 1, numberOfFiles, numberOfSubdirs, sizeOfFile);
        }
    }

    public static byte[] createDocumentContent(int sizeOfDocument) {
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);
        return bytes;
    }

    private SO_SimpleDatasafeService createOldService(String absolutePath) {
        return new SO_SimpleDatasafeServiceImpl(de.adorsys.datasafe_0_6_1.simple.adapter.api.types.FilesystemDFSCredentials.builder()
                .root(absolutePath)
                .build());

    }

    private SimpleDatasafeService createNewService(String absolutePath) {
        return new SimpleDatasafeServiceImpl(FilesystemDFSCredentials.builder()
                .root(absolutePath)
                .build(), new MutableEncryptionConfig());

    }

}
