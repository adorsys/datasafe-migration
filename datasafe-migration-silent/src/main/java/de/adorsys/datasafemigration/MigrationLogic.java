package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.S061_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.impl.S100_SimpleDatasafeServiceImpl;
import de.adorsys.datasafemigration.lockprovider.DistributedLocker;
import de.adorsys.datasafemigration.lockprovider.TemporaryLockProviderFactory;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class MigrationLogic {
    private final static int TIMEOUT_FOR_MIGRATION = 3 * 1000;

    private Set<String> migratedUsers = new HashSet<>();

    // this file has to exist for every user
    private static DocumentFQN MIGRATION_CONFIRMATION = new DocumentFQN("DATASAFE_FORMAT_1_0_0");

    private final DistributedLocker distributedLocker;
    private final GetStorage.SystemRootAndStorageService oldStorage;
    private final GetStorage.SystemRootAndStorageService newStorage;
    private final GetStorage.SystemRootAndStorageService finalStorage;
    private final S061_SimpleDatasafeService oldService;
    private final S100_SimpleDatasafeService newService;
    private final boolean withIntermediateFolder;

    public MigrationLogic(S061_DFSCredentials oldDFS, S100_DFSCredentials newDFS, MutableEncryptionConfig mutableEncryptionConfig) {
        distributedLocker = new DistributedLocker(TemporaryLockProviderFactory.get());
        {
            String oldRoot = ModifyDFSCredentials.getCurrentRootPath(ExtendedSwitchVersion.to_1_0_0(oldDFS));
            String newRoot = ModifyDFSCredentials.getCurrentRootPath(newDFS);
            withIntermediateFolder = oldRoot.equalsIgnoreCase(newRoot);
        }

        if (withIntermediateFolder) {
            newDFS = ModifyDFSCredentials.appendToRootPath(newDFS, "tempForMigrationTo100");
        }

        oldService = new S061_SimpleDatasafeServiceImpl(oldDFS);
        newService = new S100_SimpleDatasafeServiceImpl(newDFS, mutableEncryptionConfig);

        if (withIntermediateFolder) {
            finalStorage = GetStorage.get(ExtendedSwitchVersion.to_1_0_0(oldDFS));
        } else {
            finalStorage = null;
        }

        oldStorage = GetStorage.get(ExtendedSwitchVersion.to_1_0_0(oldDFS));
        newStorage = GetStorage.get(newDFS);

        log.debug("construction of migration logic {} intermediate folder", withIntermediateFolder ? "with" : "without");
        log.debug("         old root {}", oldStorage.getSystemRoot());
        if (withIntermediateFolder) {
            log.debug("intermediate root {}", newStorage.getSystemRoot());
            log.debug("         new root {}", finalStorage.getSystemRoot());
        } else {
            log.debug("         new root {}", newStorage.getSystemRoot());
        }
    }

    /**
     * This is the magic method.
     *
     * @param userIDAuth user to be migrated
     * @return If migration is alread done, it returns true.
     * If migration is not yet done. migration will be done here. After that true is returnd.
     * If migration can not be done, because schedlock credentials are not available, false is returned.
     */
    @SneakyThrows
    public boolean checkMigration(UserIDAuth userIDAuth) {
        String username = userIDAuth.getUserID().getValue();
        if (migratedUsers.contains(username)) {
            return true;
        }

        boolean gotALock = false;

        log.debug("check migration for {} not in cache yet", username);
        try {
            if (!(gotALock = distributedLocker.lockOrFail(username, TIMEOUT_FOR_MIGRATION))) {
                log.debug("as another thread/server seems to be busy with the migration of {}, we now wait at most {} millisecs", username, TIMEOUT_FOR_MIGRATION);
                for (int i = 0; i < TIMEOUT_FOR_MIGRATION * 2; i++) {
                    Thread.currentThread().sleep(500);
                    if (physicallyCheckMigrationWasDoneSuccessfully(userIDAuth)) {
                        log.debug("another thread successfully migrated user {} in the meantime", username);
                        migratedUsers.add(username);
                        return true;
                    }
                    // we did not get a lock, so we continue to wait
                }
                // we waited long enough. Migration should be finished.
                if (!physicallyCheckMigrationWasDoneSuccessfully(userIDAuth)) {
                    throw new MigrationException("we have waitet " + TIMEOUT_FOR_MIGRATION + " millisecs, but migration is not finished yet, what shall we do?");
                }
                log.debug("another thread successfully migrated user {} in the meantime", username);
                migratedUsers.add(username);
                return true;

            }

            log.debug("check migration does block now for {}", username);

            if (physicallyCheckMigrationWasDoneSuccessfully(userIDAuth)) {
                log.debug("migration for {} was already done, or user did not exist", username);
                migratedUsers.add(username);
                return true;
            }

            migrateUser(userIDAuth);
            migratedUsers.add(username);
            return true;

        } finally {
            if (gotALock) {
                distributedLocker.unlock(username);
                log.debug("check migration was unblocked for {}", username);
            }
        }
    }

    private void migrateUser(UserIDAuth userIDAuth) {
        // now we do the migration
        if (newService.userExists(userIDAuth.getUserID().getReal())) {
            throw new MigrationException("user " + userIDAuth.getUserID().getValue() + " already exists in migrated dfs, but is not yet migrated");
        }
        log.debug("NOW MIGRATION OF USER {} IS STARTED", userIDAuth.getUserID().getValue());

        LoadUserOldToNewFormat.MigrationInfo migrationInfo = new LoadUserOldToNewFormat(oldService, newService).migrateUser(userIDAuth.getReal());
        log.info(migrationInfo.toString());

        DSDocument dsDocument = new DSDocument(MIGRATION_CONFIRMATION, new DocumentContent(migrationInfo.toString().getBytes()));
        DirectDFSAccess.storeFileInUsersRootDir(newStorage, userIDAuth.getUserID(), dsDocument);

        if (withIntermediateFolder) {
            moveFromIntermediateToFinal(userIDAuth);
        } else {
            int destroyedInOld = DirectDFSAccess.destroyAllFileInUsersRootDir(oldStorage, userIDAuth.getUserID());
            log.debug("destroyed user in old location. deleted {} files.", destroyedInOld);
        }
        log.debug("NOW MIGRATION OF USER {} IS FINISHED", userIDAuth.getUserID().getValue());
    }

    /**
     * should used direct storage to be able to place the migration file in another location
     * and unencrypyted
     *
     * @param userIDAuth
     * @return
     */
    private boolean physicallyCheckMigrationWasDoneSuccessfully(UserIDAuth userIDAuth) {
        if (!(oldService.userExists(ExtendedSwitchVersion.to_0_6_1(userIDAuth.getReal().getUserID()))) &&
                !(newService.userExists(userIDAuth.getReal().getUserID()))) {
            return true;
        }
        try {
            return DirectDFSAccess.doesDocumentExistInUsersRootDir(newStorage, userIDAuth.getUserID(), MIGRATION_CONFIRMATION);
        } catch (Exception e) {
            // if the document does not exist for whatever reason, the migration is not done yet.
            return false;
        }
    }

    /**
     * should used direct storage to be able to place the migration file in another location
     * and unencrypyted
     *
     * @param userIDAuth
     * @return
     */
    public void createFileForNewUser(UserIDAuth userIDAuth) {
        StringBuilder sb = new StringBuilder();
        sb.append("user created (without migration at :").append(new Date().toString()).append("\n");
        DSDocument dsDocument = new DSDocument(MIGRATION_CONFIRMATION, new DocumentContent(sb.toString().getBytes()));
        if (withIntermediateFolder) {
            DirectDFSAccess.storeFileInUsersRootDir(finalStorage, userIDAuth.getUserID(), dsDocument);
        } else {
            DirectDFSAccess.storeFileInUsersRootDir(newStorage, userIDAuth.getUserID(), dsDocument);
        }
    }

    private void moveFromIntermediateToFinal(UserIDAuth userIDAuth) {
        int destroyedInOld = DirectDFSAccess.destroyAllFileInUsersRootDir(oldStorage, userIDAuth.getUserID());
        DirectDFSAccess.MoveInfo moveInfo = DirectDFSAccess.moveAllFiles(newStorage, oldStorage, userIDAuth.getUserID());
        int destroyedInNew = DirectDFSAccess.destroyAllFileInUsersRootDir(newStorage, userIDAuth.getUserID());
        log.info("destroyed {} files in old format of user {} ", destroyedInOld, userIDAuth.getUserID().getValue());
        log.info("moveinfo {} for user {}", moveInfo.toString(), userIDAuth.getUserID().getValue());
        log.info("destroyed {} files in new format of user {} ", destroyedInNew, userIDAuth.getUserID().getValue());
    }


}
