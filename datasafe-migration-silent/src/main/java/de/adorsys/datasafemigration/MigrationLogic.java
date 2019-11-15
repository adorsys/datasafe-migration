package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentContent;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;
import de.adorsys.datasafemigration.lockprovider.DistributedLocker;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
public class MigrationLogic {
    private final static int TIMEOUT_FOR_MIGRATION = 3 * 1000;

    private Set<UserID> migratedUsers = new HashSet<>();

    // this file has to exist for every user
    private static S100_DocumentFQN MIGRATION_CONFIRMATION = new S100_DocumentFQN("DATASAFE_FORMAT_1_0_0");

    private final DistributedLocker distributedLocker;
    private final GetStorage.SystemRootAndStorageService systemRootAndStorageService;
    private final S061_SimpleDatasafeService oldService;
    private final S100_SimpleDatasafeService newService;

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
        if (migratedUsers.contains(userIDAuth.getUserID())) {
            return true;
        }

        String username = userIDAuth.getUserID().getValue();
        boolean gotALock = false;

        try {
            if (!(gotALock = distributedLocker.lockOrFail(username, TIMEOUT_FOR_MIGRATION))) {
                log.debug("as another thread/server seems to be busy with the migration of {}, we now wait at most {} millisecs", username, TIMEOUT_FOR_MIGRATION);
                for (int i = 0; i < TIMEOUT_FOR_MIGRATION * 2; i++) {
                    Thread.currentThread().sleep(500);
                    if (physicallyCheckMigrationWasDoneSuccessfully(userIDAuth)) {
                        log.debug("another thread successfully migrated user {} in the meantime", username);
                        migratedUsers.add(userIDAuth.getUserID());
                        return true;
                    }
                    // we did not get a lock, so we continue to wait
                }
                // we waited long enough. Migration should be finished.
                if (!physicallyCheckMigrationWasDoneSuccessfully(userIDAuth)) {
                    throw new MigrationException("we have waitet " + TIMEOUT_FOR_MIGRATION + " millisecs, but migration is not finished yet, what shall we do?");
                }
                log.debug("another thread successfully migrated user {} in the meantime", username);
                migratedUsers.add(userIDAuth.getUserID());
                return true;

            }

            if (physicallyCheckMigrationWasDoneSuccessfully(userIDAuth)) {
                log.debug("migration for {} was already done, looks like first time using the user yet", username);
                migratedUsers.add(userIDAuth.getUserID());
                return true;
            }

            // now we to the migration
            if (newService.userExists(userIDAuth.getUserID().getReal())) {
                log.info("NOW MIGRATION OF USER {} IS STARTED", username);

                StringBuilder sb = new StringBuilder();
                sb.append("migration starts:").append(new Date().toString()).append("\n");
                new LoadUserOldToNewFormat(oldService, newService).migrateUser(userIDAuth.getReal());
                sb.append("migration ends:").append(new Date().toString()).append("\n");

                S100_DSDocument dsDocument = new S100_DSDocument(MIGRATION_CONFIRMATION, new S100_DocumentContent(sb.toString().getBytes(sb.toString())));
                newService.storeDocument(userIDAuth.getReal(), dsDocument);

                log.info("NOW MIGRATION OF USER {} IS FINISHED", username);
            }

            migratedUsers.add(userIDAuth.getUserID());
            return true;


        } finally {
            if (gotALock) {
                distributedLocker.unlock(username);
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
    private boolean physicallyCheckMigrationWasDoneSuccessfully(UserIDAuth userIDAuth) {
        try {
            log.info("check root is : " + systemRootAndStorageService.getSystemRoot());
            // systemRootAndStorageService.getStorageService().objectExists()
            return newService.documentExists(userIDAuth.getReal(), MIGRATION_CONFIRMATION);
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
        log.info("createFileforNew root is : " + systemRootAndStorageService.getSystemRoot());

        StringBuilder sb = new StringBuilder();
        sb.append("user created (without migration at :").append(new Date().toString()).append("\n");
        S100_DSDocument dsDocument = new S100_DSDocument(MIGRATION_CONFIRMATION, new S100_DocumentContent(sb.toString().getBytes()));
        newService.storeDocument(userIDAuth.getReal(), dsDocument);
    }
}
