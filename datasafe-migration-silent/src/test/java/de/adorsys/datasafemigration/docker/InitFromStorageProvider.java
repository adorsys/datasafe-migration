package de.adorsys.datasafemigration.docker;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_AmazonS3DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.exceptions.SimpleAdapterException;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.FilesystemDFSCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class InitFromStorageProvider {

    public DFSCredentialsTuple dfsFromDescriptor(WithStorageProvider.StorageDescriptor descriptor, String newSubfolder, String oldSubfolder) {
        if (descriptor == null) {
            return null;
        }

        if (oldSubfolder == null) {
            oldSubfolder = "";
        }
        if (!oldSubfolder.startsWith("/"))  {
            oldSubfolder = "/" + oldSubfolder;
        }
        if (newSubfolder == null) {
            newSubfolder = "";
        }
        if (!newSubfolder.startsWith("/"))  {
            newSubfolder = "/" + newSubfolder;
        }

        switch (descriptor.getName()) {
            case FILESYSTEM: {
                log.info("uri:" + descriptor.getRootBucket());
                return new DFSCredentialsTuple(
                        SO_FilesystemDFSCredentials.builder().root(descriptor.getRootBucket() + oldSubfolder).build(),
                        FilesystemDFSCredentials.builder().root(descriptor.getRootBucket() + newSubfolder).build()
                );

            }
            case MINIO:
            case CEPH:
            case AMAZON: {
                descriptor.getStorageService().get();
                log.info("uri       :" + descriptor.getLocation());
                log.info("accesskey :" + descriptor.getAccessKey());
                log.info("secretkey :" + descriptor.getSecretKey());
                log.info("region    :" + descriptor.getRegion());
                log.info("old rootbucket:" + descriptor.getRootBucket() + newSubfolder);
                log.info("new rootbucket:" + descriptor.getRootBucket() + oldSubfolder);
                log.info("mapped uri:" + descriptor.getMappedUrl());

                return new DFSCredentialsTuple(
                        SO_AmazonS3DFSCredentials.builder()
                                .accessKey(descriptor.getAccessKey())
                                .secretKey(descriptor.getSecretKey())
                                .region(descriptor.getRegion())
                                .rootBucket(descriptor.getRootBucket() + oldSubfolder)
                                .url(descriptor.getMappedUrl())
                                .build(),
                        AmazonS3DFSCredentials.builder()
                                .accessKey(descriptor.getAccessKey())
                                .secretKey(descriptor.getSecretKey())
                                .region(descriptor.getRegion())
                                .rootBucket(descriptor.getRootBucket() + newSubfolder)
                                .url(descriptor.getMappedUrl())
                                .build());
            }
            default:
                throw new SimpleAdapterException("missing switch for " + descriptor.getName());
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DFSCredentialsTuple {
        private final SO_DFSCredentials oldVersion;
        private final DFSCredentials newVersion;

    }
}
