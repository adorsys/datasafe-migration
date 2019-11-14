package de.adorsys.datasafe.simple.adapter.impl;


import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafemigration.ExtendedSwitchVersion;
import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocumentStream;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.SO_UserID;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.SO_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.impl.SimpleDatasafeServiceImpl;
import de.adorsys.datasafemigration.MigrationLogic;
import de.adorsys.datasafemigration.common.SwitchVersion;
import de.adorsys.datasafemigration.lockprovider.DistributedLocker;
import de.adorsys.datasafemigration.lockprovider.TemporaryLockProviderFactory;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.adorsys.datasafemigration.ExtendedSwitchVersion.toCurrent;

public class SimpleDatasafeServiceWithMigration implements SimpleDatasafeService {
    private de.adorsys.datasafe_1_0_0.simple.adapter.api.SimpleDatasafeService newReal;
    private de.adorsys.datasafe_0_6_1.simple.adapter.api.SO_SimpleDatasafeService oldReal;
    private MigrationLogic migrationLogic;


    public SimpleDatasafeServiceWithMigration(DFSCredentials dfsCredentials, MutableEncryptionConfig mutableEncryptionConfig) {
        newReal = new SimpleDatasafeServiceImpl(dfsCredentials, mutableEncryptionConfig);
        oldReal = new SO_SimpleDatasafeServiceImpl(ExtendedSwitchVersion.to_0_6_1(dfsCredentials));

        DistributedLocker distributedLocker = new DistributedLocker(TemporaryLockProviderFactory.get());
        migrationLogic = new MigrationLogic(distributedLocker, GetStorage.get(dfsCredentials), oldReal, newReal);
    }


    @Override
    public void createUser(UserIDAuth userIDAuth) {
        if (checkMigration(userIDAuth)) {
            newReal.createUser(userIDAuth.getReal());
            migrationLogic.createFileForNewUser(userIDAuth);
            return;
        }
        oldReal.createUser(SwitchVersion.to_0_6_1(userIDAuth.getReal()));
    }


    @Override
    public void destroyUser(UserIDAuth userIDAuth) {
        if (checkMigration(userIDAuth)) {
            newReal.destroyUser(userIDAuth.getReal());
            return;
        }
        oldReal.destroyUser(SwitchVersion.to_0_6_1(userIDAuth.getReal()));
    }

    @Override
    public boolean userExists(UserID userID) {
        boolean result = false;
        if (oldReal != null) {
            try {
                result = oldReal.userExists(new SO_UserID(userID.getReal().getValue()));
            } catch (Exception e) {
                // ignored by purpose
            }
            if (result) {
                return result;
            }
        }
        if (newReal != null) {
                return newReal.userExists(userID.getReal());
        }
        throw new RuntimeException("dont know what to do");
    }

    @Override
    public void storeDocument(UserIDAuth userIDAuth, DSDocument dsDocument) {
        if (checkMigration(userIDAuth)) {
            newReal.storeDocument(userIDAuth.getReal(), dsDocument.getReal());
            return;
        }
        oldReal.storeDocument(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(dsDocument.getReal()));
    }

    @Override
    public DSDocument readDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        if (checkMigration(userIDAuth)) {
            return ExtendedSwitchVersion.toCurrent(newReal.readDocument(userIDAuth.getReal(), documentFQN.getReal()));
        }
        return ExtendedSwitchVersion.toCurrent(
                oldReal.readDocument(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(documentFQN.getReal())));
    }

    @Override
    public OutputStream storeDocumentStream(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        if (checkMigration(userIDAuth)) {
            return newReal.storeDocumentStream(userIDAuth.getReal(), documentFQN.getReal());
        }
        return oldReal.storeDocumentStream(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(documentFQN.getReal()));
    }

    @Override
    public DSDocumentStream readDocumentStream(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        if (checkMigration(userIDAuth)) {
            return ExtendedSwitchVersion.toCurrent(newReal.readDocumentStream(userIDAuth.getReal(), documentFQN.getReal()));
        }
        return toCurrent(oldReal.readDocumentStream(
                SwitchVersion.to_0_6_1(userIDAuth.getReal()),
                ExtendedSwitchVersion.to_0_6_1(documentFQN.getReal())
        ));
    }

    @Override
    public void storeDocumentStream(UserIDAuth userIDAuth, DSDocumentStream dsDocumentStream) {
        if (checkMigration(userIDAuth)) {
            newReal.storeDocumentStream(userIDAuth.getReal(), dsDocumentStream.getReal());
            return;
        }
        oldReal.storeDocumentStream(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(dsDocumentStream.getReal()));

    }

    @Override
    public void deleteDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        if (checkMigration(userIDAuth)) {
            newReal.deleteDocument(userIDAuth.getReal(), documentFQN.getReal());
            return;
        }
        oldReal.deleteDocument(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(documentFQN.getReal()));


    }

    @Override
    public boolean documentExists(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        if (checkMigration(userIDAuth)) {
            return newReal.documentExists(userIDAuth.getReal(), documentFQN.getReal());
        }
        return oldReal.documentExists(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(documentFQN.getReal()));
    }

    @Override
    public void deleteFolder(UserIDAuth userIDAuth, DocumentDirectoryFQN documentDirectoryFQN) {
        if (checkMigration(userIDAuth)) {
            newReal.deleteFolder(userIDAuth.getReal(), documentDirectoryFQN.getReal());
            return;
        }
        oldReal.deleteFolder(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(documentDirectoryFQN.getReal()));
    }

    @Override
    public List<DocumentFQN> list(UserIDAuth userIDAuth, DocumentDirectoryFQN documentDirectoryFQN, ListRecursiveFlag listRecursiveFlag) {
        if (checkMigration(userIDAuth)) {
            List<DocumentFQN> result = new ArrayList<>();
            newReal.list(userIDAuth.getReal(), documentDirectoryFQN.getReal(), ExtendedSwitchVersion.to_1_0_0(listRecursiveFlag)).forEach(
                    el -> result.add(new DocumentFQN(el.getDocusafePath()))
            );
            return result;
        }
        List<DocumentFQN> result = new ArrayList<>();
        newReal.list(userIDAuth.getReal(), documentDirectoryFQN.getReal(), ExtendedSwitchVersion.to_1_0_0(listRecursiveFlag)).forEach(
                el -> result.add(new DocumentFQN(el.getDocusafePath()))
        );
        return result;
    }

    @Override
    public void cleanupDb() {
        if (newReal != null) {
            newReal.cleanupDb();
        }
        if (oldReal != null) {
            oldReal.cleanupDb();
        }

    }

    @Override
    public void changeKeystorePassword(UserIDAuth userIDAuth, ReadKeyPassword readKeyPassword) {
        if (checkMigration(userIDAuth)) {
            newReal.changeKeystorePassword(userIDAuth.getReal(), readKeyPassword.getReal());
            return;
        }
        oldReal.changeKeystorePassword(SwitchVersion.to_0_6_1(userIDAuth.getReal()), ExtendedSwitchVersion.to_0_6_1(readKeyPassword.getReal()));

    }

    private boolean checkMigration(UserIDAuth userIDAuth) {
        return migrationLogic.checkMigration(userIDAuth);
    }

}
