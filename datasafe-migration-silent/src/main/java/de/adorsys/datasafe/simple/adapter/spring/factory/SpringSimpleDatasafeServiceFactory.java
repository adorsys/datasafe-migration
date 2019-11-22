package de.adorsys.datasafe.simple.adapter.spring.factory;


import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.exceptions.SimpleAdapterException;
import de.adorsys.datasafe.simple.adapter.impl.DatasafeMigrationConfig;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.properties.SpringDatasafeEncryptionProperties;
import de.adorsys.datasafemigration.MigrationException;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
public class SpringSimpleDatasafeServiceFactory {
    @Autowired
    S100_DFSCredentials wiredDfsCredentials;

    @Autowired
    SpringDatasafeEncryptionProperties encryptionProperties;

    @Autowired
    DatasafeMigrationConfig datasafeMigrationConfig;

    S100_DFSCredentials dfsCredentials;

    boolean useWiredCredentials = true;

    @PostConstruct
    public void postConstruct() {
        log.info("POST CONSTRUCT OF SpringSimpleDatasafeServiceFactory");
        if (useWiredCredentials) {
            if (wiredDfsCredentials == null) {
                throw new RuntimeException("wiredDfsCredentials are nulL, so injection did not work");
            }
            dfsCredentials = wiredDfsCredentials;
        }
        if (datasafeMigrationConfig == null) {
            throw new MigrationException("Injection for LockProvider did not work");
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
        log.info("CTOR of SpringSimpleDatasafeServiceFactory");
    }

    public SimpleDatasafeService getSimpleDataSafeServiceWithSubdir(String subdirBelowRoot) {
        if (dfsCredentials instanceof S100_AmazonS3DFSCredentials) {
            S100_AmazonS3DFSCredentials amazonS3DFSCredentials = (S100_AmazonS3DFSCredentials) dfsCredentials;
            return new SimpleDatasafeServiceWithMigration(
                    datasafeMigrationConfig,
                    amazonS3DFSCredentials.toBuilder().rootBucket(
                            amazonS3DFSCredentials.getRootBucket() + "/" + subdirBelowRoot
                    ).build(),
                    null != encryptionProperties ? encryptionProperties.getEncryption() : new MutableEncryptionConfig()
            );
        }
        if (dfsCredentials instanceof S100_FilesystemDFSCredentials) {
            S100_FilesystemDFSCredentials filesystemDFSCredentials = (S100_FilesystemDFSCredentials) dfsCredentials;
            return new SimpleDatasafeServiceWithMigration(
                    datasafeMigrationConfig,
                    filesystemDFSCredentials.toBuilder().root(
                            filesystemDFSCredentials.getRoot() + "/" + subdirBelowRoot
                    ).build(),
                    null != encryptionProperties ? encryptionProperties.getEncryption() : new MutableEncryptionConfig()
            );
        }
        throw new SimpleAdapterException("missing switch for DFSCredentials" + dfsCredentials);
    }

}
