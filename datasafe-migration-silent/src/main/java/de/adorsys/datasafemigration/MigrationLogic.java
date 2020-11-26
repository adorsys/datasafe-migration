package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.S061_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.S103_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.impl.LogStringFrame;
import de.adorsys.datasafe_1_0_3.simple.adapter.impl.S103_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_3.simple.adapter.impl.config.S103_PathEncryptionConfig;
import de.adorsys.datasafemigration.lockprovider.DistributedLocker;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class MigrationLogic {
    private final int timeout;

    private Set<String> migratedUsers = new HashSet<>();

    // this file has to exist for every user
    private static final DocumentFQN MIGRATION_CONFIRMATION = new DocumentFQN("DATASAFE_FORMAT_1_0_3");
    private static final DocumentFQN MIGRATION_CONFIRMATION_1_0_1 = new DocumentFQN("DATASAFE_FORMAT_1_0_1");

    private final DistributedLocker distributedLocker;
    private final GetStorage.SystemRootAndStorageService oldStorage;
    private final GetStorage.SystemRootAndStorageService newStorage;
    private final GetStorage.SystemRootAndStorageService finalStorage;
    private final S061_SimpleDatasafeService oldService;
    private final S103_SimpleDatasafeService newService;
    private final boolean withIntermediateFolder;
    private final boolean migrationPossible;

    public MigrationLogic(LockProvider lockProvider, int timeout, S061_DFSCredentials oldDFS, S103_DFSCredentials newDFS, MutableEncryptionConfig mutableEncryptionConfig,
                          S103_PathEncryptionConfig pathEncryptionConfig) {
        LogStringFrame lsf = new LogStringFrame();
        if (lockProvider != null) {
            this.timeout = timeout;
            distributedLocker = new DistributedLocker(lockProvider);
            migrationPossible = true;

            {
                String oldRoot = ModifyDFSCredentials.getCurrentRootPath(ExtendedSwitchVersion.to_1_0_3(oldDFS));
                String newRoot = ModifyDFSCredentials.getCurrentRootPath(newDFS);
                withIntermediateFolder = oldRoot.equalsIgnoreCase(newRoot);
            }

            if (withIntermediateFolder) {
                newDFS = ModifyDFSCredentials.appendToRootPath(newDFS, "tempForMigrationTo100");
            }

            oldService = new S061_SimpleDatasafeServiceImpl(oldDFS);
            newService = new S103_SimpleDatasafeServiceImpl(newDFS, mutableEncryptionConfig, pathEncryptionConfig);

            if (withIntermediateFolder) {
                finalStorage = GetStorage.get(ExtendedSwitchVersion.to_1_0_3(oldDFS));
            } else {
                finalStorage = null;
            }

            oldStorage = GetStorage.get(ExtendedSwitchVersion.to_1_0_3(oldDFS));
            newStorage = GetStorage.get(newDFS);


            lsf.add("MigrationLogic      : ENABLED");
            lsf.add("  migration timeout : " + timeout);
            lsf.add("intermediate folder : " + (withIntermediateFolder ? "YES" : "NO"));
            lsf.add("           old root : " + oldStorage.getSystemRoot());
            if (withIntermediateFolder) {
                lsf.add("  intermediate root : " + newStorage.getSystemRoot());
                lsf.add("           new root : " + finalStorage.getSystemRoot());
            } else {
                lsf.add("           new root : " + newStorage.getSystemRoot());
            }
        } else {
            this.timeout = 0;
            oldStorage = null;
            newStorage = null;
            finalStorage = null;
            oldService = null;
            newService = null;
            withIntermediateFolder = false;
            distributedLocker = null;
            migrationPossible = false;
            lsf.add("MigrationLogic      : DISABLED");
        }
        log.info(lsf.toString());
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
        if (!migrationPossible) {
            return false;
        }

        String username = userIDAuth.getUserID().getValue();
        if (migratedUsers.contains(username)) {
            return true;
        }

        // before now accessing a lock to work eclusivly, we can check if user is already migrated
        if (withIntermediateFolder) {
            if (DirectDFSAccess.doesDocumentExistInUsersRootDir(finalStorage, userIDAuth.getUserID(), MIGRATION_CONFIRMATION_1_0_1) ||
                    DirectDFSAccess.doesDocumentExistInUsersRootDir(finalStorage, userIDAuth.getUserID(), MIGRATION_CONFIRMATION)) {
                log.debug("user {} is already migrated, found version file in finalStorage", username);
                migratedUsers.add(username);
                return true;
            }
        } else {
            if (DirectDFSAccess.doesDocumentExistInUsersRootDir(newStorage, userIDAuth.getUserID(), MIGRATION_CONFIRMATION_1_0_1) ||
                    DirectDFSAccess.doesDocumentExistInUsersRootDir(newStorage, userIDAuth.getUserID(), MIGRATION_CONFIRMATION)) {
                log.debug("user {} is already migrated, found version file in newStorage", username);
                migratedUsers.add(username);
                return true;
            }
        }
        // now as the user does not exist yet, we have to get a lock

        boolean gotALock = false;

        log.debug("check migration for {} not in cache yet", username);
        try {
            if (!(gotALock = distributedLocker.lockOrFail(username, timeout))) {
                log.debug("as another thread/server seems to be busy with the migration of {}, we now wait at most {} millisecs", username, timeout);
                int waitedInMillis = 0;
                while (waitedInMillis < timeout) {
                    Thread.currentThread().sleep(500);
                    if (physicallyCheckMigrationWasDoneSuccessfully(userIDAuth.getUserID())) {
                        log.debug("another thread successfully migrated user {} in the meantime", username);
                        migratedUsers.add(username);
                        return true;
                    }
                    waitedInMillis+= 500;
                    // we did not get a lock and user is not migrated, we have to wait
                }
                // we waited long enough. Migration should be finished.
                if (!physicallyCheckMigrationWasDoneSuccessfully(userIDAuth.getUserID())) {
                    throw new MigrationException("we have waited " + waitedInMillis + " millisecs, but migration is not finished yet, what shall we do?");
                }
                log.debug("another thread successfully migrated user {} in the meantime", username);
                migratedUsers.add(username);
                return true;

            }

            log.debug("check migration does block now for {}", username);

            if (physicallyCheckMigrationWasDoneSuccessfully(userIDAuth.getUserID())) {
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

    /**
     * should used direct storage to be able to place the migration file in another location
     * and unencrypyted
     *
     * @param userIDAuth
     * @return
     */
    public void createFileForNewUser(UserIDAuth userIDAuth) {
        if (!migrationPossible) {
            throw new MigrationException("create new File must not be called if migration is not enabled");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("user created (without migration at :").append(new Date().toString()).append("\n");
        DSDocument dsDocument = new DSDocument(MIGRATION_CONFIRMATION, new DocumentContent(sb.toString().getBytes()));
        if (withIntermediateFolder) {
            DirectDFSAccess.storeFileInUsersRootDir(finalStorage, userIDAuth.getUserID(), dsDocument);
        } else {
            DirectDFSAccess.storeFileInUsersRootDir(newStorage, userIDAuth.getUserID(), dsDocument);
        }
    }

    private void migrateUser(UserIDAuth userIDAuth) {
        // now we do the migration
        if (newService.userExists(userIDAuth.getUserID().getReal())) {
            throw new MigrationException("user " + userIDAuth.getUserID().getValue() + " already exists in migrated dfs, but is not yet migrated");
        }
        log.debug("NOW MIGRATION OF USER {} IS STARTED", userIDAuth.getUserID().getValue());
        Instant start = Instant.now();

        LoadUserOldToNewFormat.MigrationInfo migrationInfo = new LoadUserOldToNewFormat(oldService, newService).migrateUser(userIDAuth.getReal());
        log.info(migrationInfo.toString());

        DSDocument dsDocument = new DSDocument(MIGRATION_CONFIRMATION, new DocumentContent(migrationInfo.toString().getBytes()));
        DirectDFSAccess.storeFileInUsersRootDir(newStorage, userIDAuth.getUserID(), dsDocument);

        DirectDFSAccess.MoveInfo moveInfo = null;
        if (withIntermediateFolder) {
            moveInfo = moveFromIntermediateToFinal(userIDAuth);
        } else {
            int destroyedInOld = DirectDFSAccess.destroyAllFileInUsersRootDir(oldStorage, userIDAuth.getUserID());
            log.debug("destroyed user in old location. deleted {} files.", destroyedInOld);
        }

        Duration totalTimeOfMigration = Duration.between(start, Instant.now());

        log.debug("NOW MIGRATION OF USER {} IS FINISHED", userIDAuth.getUserID().getValue());
        long totalMillis = totalTimeOfMigration.toMillis();
        long migrationMillis = migrationInfo.getDuration().toMillis();
        long filemovementMillis = totalMillis - migrationMillis;
        log.info("MIGRATION OF {} FILES FOR USER {} TOOK {} MILLIS. Migration itself took {} millis and relocation of files took {} millis.", migrationInfo.getFiles(), userIDAuth.getUserID().getValue(), totalMillis, migrationMillis, filemovementMillis);
    }

    /**
     * should used direct storage to be able to place the migration file in another location
     * and unencrypyted
     *
     * @param userID
     * @return
     */
    @SuppressWarnings("Duplicates")
    private boolean physicallyCheckMigrationWasDoneSuccessfully(UserID userID) {
        if (withIntermediateFolder) {
            if (DirectDFSAccess.doesDocumentExistInUsersRootDir(finalStorage, userID, MIGRATION_CONFIRMATION)) {
                log.debug("user {} is already migrated");
                return true;
            }
            if (!DirectDFSAccess.doesUserExist(finalStorage, userID)) {
                log.debug("user {} does not exist at all and thus is to be regarded as migrated");
                return true;
            }
            log.debug("user {} is not yet migrated", userID);
            return false;
        } else {
            if (DirectDFSAccess.doesDocumentExistInUsersRootDir(newStorage, userID, MIGRATION_CONFIRMATION)) {
                log.debug("user {} is already migrated");
                return true;
            }
            if (!DirectDFSAccess.doesUserExist(oldStorage, userID)) {
                log.debug("user {} does not exist at all and thus is to be regarded as migrated");
                return true;
            }
            log.debug("user {} is not yet migrated", userID);
            return false;
        }
    }

    private DirectDFSAccess.MoveInfo moveFromIntermediateToFinal(UserIDAuth userIDAuth) {
        int destroyedInOld = DirectDFSAccess.destroyAllFileInUsersRootDir(oldStorage, userIDAuth.getUserID());
        DirectDFSAccess.MoveInfo moveInfo = DirectDFSAccess.moveAllFiles(newStorage, oldStorage, userIDAuth.getUserID());
        int destroyedInNew = DirectDFSAccess.destroyAllFileInUsersRootDir(newStorage, userIDAuth.getUserID());
        log.info("destroyed {} files in old format of user {} ", destroyedInOld, userIDAuth.getUserID().getValue());
        log.info("moveinfo {} for user {}", moveInfo.toString(), userIDAuth.getUserID().getValue());
        log.info("destroyed {} files in new format of user {} ", destroyedInNew, userIDAuth.getUserID().getValue());
        return moveInfo;
    }


}
