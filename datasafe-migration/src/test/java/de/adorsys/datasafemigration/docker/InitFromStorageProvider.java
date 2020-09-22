package de.adorsys.datasafemigration.docker;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_AmazonS3DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.exceptions.SimpleAdapterException;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_FilesystemDFSCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

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
        if (!oldSubfolder.startsWith("/")) {
            oldSubfolder = "/" + oldSubfolder;
        }
        if (newSubfolder == null) {
            newSubfolder = "";
        }
        if (!newSubfolder.startsWith("/")) {
            newSubfolder = "/" + newSubfolder;
        }

        switch (descriptor.getName()) {
            case FILESYSTEM: {
                log.info("uri:" + descriptor.getRootBucket());
                return new DFSCredentialsTuple(
                        S061_FilesystemDFSCredentials.builder().root(descriptor.getRootBucket() + oldSubfolder).build(),
                        S103_FilesystemDFSCredentials.builder().root(descriptor.getRootBucket() + newSubfolder).build()
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
                        S061_AmazonS3DFSCredentials.builder()
                                .accessKey(descriptor.getAccessKey())
                                .secretKey(descriptor.getSecretKey())
                                .region(descriptor.getRegion())
                                .rootBucket(descriptor.getRootBucket() + oldSubfolder)
                                .url(descriptor.getMappedUrl())
                                .build(),
                        S103_AmazonS3DFSCredentials.builder()
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
        private final S061_DFSCredentials oldVersion;
        private final S103_DFSCredentials newVersion;

    }
}
