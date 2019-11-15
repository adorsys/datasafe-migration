package de.adorsys.datasafe.simple.adapter.spring.factory;


import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.exceptions.SimpleAdapterException;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.properties.SpringDatasafeEncryptionProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class SpringSimpleDatasafeServiceFactory {
    @Autowired
    S100_DFSCredentials wiredDfsCredentials;

    @Autowired
    SpringDatasafeEncryptionProperties encryptionProperties;

    S100_DFSCredentials dfsCredentials;

    boolean useWiredCredentials = true;

    @PostConstruct
    public void postConstruct() {
        if (useWiredCredentials) {
            if (wiredDfsCredentials == null) {
                throw new RuntimeException("wiredDfsCredentials are nulL, so injection did not work");
            }
            dfsCredentials = wiredDfsCredentials;
        }
    }

    public SpringSimpleDatasafeServiceFactory() {
        useWiredCredentials = true;
    }

    public SpringSimpleDatasafeServiceFactory(S100_DFSCredentials credentials) {
        if (credentials == null) {
            throw new RuntimeException("dfs credentials passed in must not be null");
        }
        dfsCredentials = credentials;
        useWiredCredentials = false;
    }

    public SimpleDatasafeService getSimpleDataSafeServiceWithSubdir(String subdirBelowRoot) {
        if (dfsCredentials instanceof S100_AmazonS3DFSCredentials) {
            S100_AmazonS3DFSCredentials amazonS3DFSCredentials = (S100_AmazonS3DFSCredentials) dfsCredentials;
            return new SimpleDatasafeServiceWithMigration(
                    amazonS3DFSCredentials.toBuilder().rootBucket(
                            amazonS3DFSCredentials.getRootBucket() + "/" + subdirBelowRoot + "/061"
                    ).build(),
                    null != encryptionProperties ? encryptionProperties.getEncryption() : new MutableEncryptionConfig()
            );
        }
        if (dfsCredentials instanceof S100_FilesystemDFSCredentials) {
            S100_FilesystemDFSCredentials filesystemDFSCredentials = (S100_FilesystemDFSCredentials) dfsCredentials;
            return new SimpleDatasafeServiceWithMigration(
                    filesystemDFSCredentials.toBuilder().root(
                            filesystemDFSCredentials.getRoot() + "/" + subdirBelowRoot + "/100"
                    ).build(),
                    null != encryptionProperties ? encryptionProperties.getEncryption() : new MutableEncryptionConfig()
            );
        }
        throw new SimpleAdapterException("missing switch for DFSCredentials" + dfsCredentials);
    }

}
