package de.adorsys.datasafemigration;


import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.S061_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserID;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.S103_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_3.simple.adapter.impl.S103_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_3.simple.adapter.impl.config.S103_PathEncryptionConfig;
import de.adorsys.datasafe_1_0_3.types.api.types.S103_ReadKeyPassword;
import de.adorsys.datasafemigration.common.SwitchVersion;
import de.adorsys.datasafemigration.docker.InitFromStorageProvider;
import de.adorsys.datasafemigration.docker.WithStorageProvider;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import de.adorsys.datasafemigration.withlocalfilesystem.LoadNewUserToLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteOldUserFromLocal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MigrationTest extends WithStorageProvider {

    private Path tempDir;
    private String oldSubFolder = "0.6.1";
    private String newSubFolder = "1.0.1";


    @SneakyThrows
    @BeforeEach
    public void init() {
        tempDir = Files.createTempDirectory("migration-test");
    }


    @ParameterizedTest
    @MethodSource("allStorages")
    public void testMigrationWithLocalFiles(WithStorageProvider.StorageDescriptor descriptor) {
        InitFromStorageProvider.DFSCredentialsTuple dfsCredentialsTuple =  InitFromStorageProvider.dfsFromDescriptor(descriptor, oldSubFolder, newSubFolder);

        S103_UserIDAuth userIDAuth = new S103_UserIDAuth(new S103_UserID("peter"), new S103_ReadKeyPassword("password"::toCharArray));
        S061_SimpleDatasafeService oldService = new S061_SimpleDatasafeServiceImpl(dfsCredentialsTuple.getOldVersion());
        S103_SimpleDatasafeService newService = new S103_SimpleDatasafeServiceImpl(dfsCredentialsTuple.getNewVersion(), new MutableEncryptionConfig(), new S103_PathEncryptionConfig(true));
        S103_DocumentDirectoryFQN startDatadir;
        {
            // Test preparation

            // create tree of files for one user on local disk (below tempDir)
            startDatadir = new S103_DocumentDirectoryFQN(tempDir.toString()).addDirectory("startupfiles");
            createLocalFilesInFolder(startDatadir.addDirectory(userIDAuth.getUserID().getValue()), 3, 3, 2, 1000);

            // move file tree of user to old datasafe format. destination depending on dataservice config
            WriteOldUserFromLocal oldWriter = new WriteOldUserFromLocal(oldService, startDatadir);
            oldWriter.migrateUser(userIDAuth);
        }

        {
            // Migration itself
            LoadUserOldToNewFormat migrator = new LoadUserOldToNewFormat(oldService, newService);
            migrator.migrateUser(userIDAuth);
        }

        {
            // Test result

            // load all data from new filetree to local disk
            S103_DocumentDirectoryFQN destDatadir = new S103_DocumentDirectoryFQN(tempDir.toString()).addDirectory("loadedfromnewfiles");
            LoadNewUserToLocal loadNewUserToLocal = new LoadNewUserToLocal(newService, destDatadir);
            loadNewUserToLocal.migrateUser(userIDAuth);

            // compare initial tree to reloaded tree on local disk
            compare(startDatadir, destDatadir);
        }
    }

    @ParameterizedTest
    @MethodSource("allStorages")
    public void testMigrationWithMemory(WithStorageProvider.StorageDescriptor descriptor) {
        InitFromStorageProvider.DFSCredentialsTuple dfsCredentialsTuple = InitFromStorageProvider.dfsFromDescriptor(descriptor, oldSubFolder, newSubFolder);

        Set<S061_UserIDAuth> listOfOldUsers = CreateStructureUtil.getS061_userIDAuths();
        Set<S103_UserIDAuth> listOfNewUsers = new HashSet<>();
        listOfOldUsers.forEach(s061_userIDAuth -> listOfNewUsers.add(SwitchVersion.to_1_0_3(s061_userIDAuth)));

        S061_SimpleDatasafeService s061_simpleDatasafeService = new S061_SimpleDatasafeServiceImpl(dfsCredentialsTuple.getOldVersion());
        S103_SimpleDatasafeService s103_simpleDatasafeService = new S103_SimpleDatasafeServiceImpl(dfsCredentialsTuple.getNewVersion(), new MutableEncryptionConfig(),
            new S103_PathEncryptionConfig(true));
        Map<S061_UserIDAuth, Set<S061_DSDocument>> s061StructureMap = CreateStructureUtil.create061Structure(s061_simpleDatasafeService, listOfOldUsers);

        for (S103_UserIDAuth s103_userIDAuth : listOfNewUsers) {
            LoadUserOldToNewFormat migrator = new LoadUserOldToNewFormat(s061_simpleDatasafeService, s103_simpleDatasafeService);
            migrator.migrateUser(s103_userIDAuth);
        }

        Map<S103_UserIDAuth, Set<S103_DSDocument>> s103_Structuremap = LoadStructureUtil.loadS100Structure(s103_simpleDatasafeService, listOfNewUsers);

        for (S061_UserIDAuth s061_userIDAuth : listOfOldUsers) {
            Set<S061_DSDocument> s061_dsDocuments = s061StructureMap.get(s061_userIDAuth);
            Set<S103_DSDocument> s103_dsDocuments = s103_Structuremap.get(SwitchVersion.to_1_0_3(s061_userIDAuth));
            compare(s061_dsDocuments, s103_dsDocuments);
        }
    }


    @ParameterizedTest
    @MethodSource("allStorages")
    public void testIncompatibility(WithStorageProvider.StorageDescriptor descriptor) {
        InitFromStorageProvider.DFSCredentialsTuple dfsCredentialsTuple = InitFromStorageProvider.dfsFromDescriptor(descriptor, oldSubFolder, oldSubFolder);

        S103_UserIDAuth userIDAuth = new S103_UserIDAuth(new S103_UserID("peter"), new S103_ReadKeyPassword("password"::toCharArray));
        S061_SimpleDatasafeService oldService = new S061_SimpleDatasafeServiceImpl(dfsCredentialsTuple.getOldVersion());
        S103_DocumentDirectoryFQN startDatadir;
        {
            // Test preparation

            // create tree of files for one user on local disk (below tempDir)
            startDatadir = new S103_DocumentDirectoryFQN(tempDir.toString()).addDirectory("startupfiles");
            createLocalFilesInFolder(startDatadir.addDirectory(userIDAuth.getUserID().getValue()), 3, 3, 2, 1000);

            // move file tree of user to old datasafe format. destination depending on dataservice config
            WriteOldUserFromLocal oldWriter = new WriteOldUserFromLocal(oldService, startDatadir);
            oldWriter.migrateUser(userIDAuth);
        }

        S103_SimpleDatasafeService newService = new S103_SimpleDatasafeServiceImpl(dfsCredentialsTuple.getNewVersion(), new MutableEncryptionConfig(),
            new S103_PathEncryptionConfig(true));
        Assertions.assertThrows(IOException.class, () -> newService.list(
                userIDAuth,
                new S103_DocumentDirectoryFQN("/"),
                S103_ListRecursiveFlag.TRUE));

    }

    @SneakyThrows
    private void compare(Set<S061_DSDocument> s061_dsDocuments, Set<S103_DSDocument> s103_dsDocuments) {
        int counter = 0;
        long bytecounter = 0;
        for (S061_DSDocument s061_dsDocument : s061_dsDocuments) {
            S061_DocumentFQN s061_dsDocumentDocumentFQN = s061_dsDocument.getDocumentFQN();
            boolean documentFound = false;
            for (S103_DSDocument s103_dsDocument : s103_dsDocuments) {
                if (SwitchVersion.to_0_6_1(s103_dsDocument.getDocumentFQN()).equals(s061_dsDocumentDocumentFQN)) {
                    Assertions.assertArrayEquals(s061_dsDocument.getDocumentContent().getValue(),
                            s103_dsDocument.getDocumentContent().getValue());
                    documentFound = true;
                    counter++;
                    bytecounter += s061_dsDocument.getDocumentContent().getValue().length;
                }
            }
            if (!documentFound) {
                throw new RuntimeException("Did not find document " + s061_dsDocumentDocumentFQN);
            }
        }
        log.info("successfully compared {} documents with {} bytes in total", counter, bytecounter);
    }

    @SneakyThrows
    private void compare(S103_DocumentDirectoryFQN srcFolder, S103_DocumentDirectoryFQN destFolder) {
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
            S103_DocumentFQN srcFQN = srcFolder.addName(el);
            S103_DocumentFQN destFQN = destFolder.addName(el);

            byte[] srcBytes = Files.readAllBytes(Paths.get(srcFQN.getDocusafePath()));
            byte[] destBytes = Files.readAllBytes(Paths.get(destFQN.getDocusafePath()));

            Assertions.assertArrayEquals(srcBytes, destBytes);
            counter++;
        }

        log.info("all {} files have same content :-)", counter);
    }


    @SneakyThrows
    private static void createLocalFilesInFolder(S103_DocumentDirectoryFQN path, int recursiveDepth, int numberOfFiles, int numberOfSubdirs, int sizeOfFile) {
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
}
