package de.adorsys.datasafe.simple.adapter.impl;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.*;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;

import java.io.OutputStream;
import java.util.List;

public class SimpleDatasafeServiceWithMigration implements SimpleDatasafeService {
    private de.adorsys.datasafe_1_0_0.simple.adapter.api.SimpleDatasafeService originalSimpleDatasafeService;

    @Override
    public void createUser(UserIDAuth userIDAuth) {

    }

    @Override
    public void destroyUser(UserIDAuth userIDAuth) {

    }

    @Override
    public boolean userExists(UserID userID) {
        return false;
    }

    @Override
    public void storeDocument(UserIDAuth userIDAuth, DSDocument dsDocument) {

    }

    @Override
    public DSDocument readDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        return null;
    }

    @Override
    public OutputStream storeDocumentStream(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        return null;
    }

    @Override
    public DSDocumentStream readDocumentStream(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        return null;
    }

    @Override
    public void storeDocumentStream(UserIDAuth userIDAuth, DSDocumentStream dsDocumentStream) {

    }

    @Override
    public void deleteDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN) {

    }

    @Override
    public boolean documentExists(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
        return false;
    }

    @Override
    public void deleteFolder(UserIDAuth userIDAuth, DocumentDirectoryFQN documentDirectoryFQN) {

    }

    @Override
    public List<DocumentFQN> list(UserIDAuth userIDAuth, DocumentDirectoryFQN documentDirectoryFQN, ListRecursiveFlag listRecursiveFlag) {
        return null;
    }

    @Override
    public void cleanupDb() {

    }

    @Override
    public void changeKeystorePassword(UserIDAuth userIDAuth, ReadKeyPassword readKeyPassword) {

    }
}
