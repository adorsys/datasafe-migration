package de.adorsys.datasafe.simple.adapter.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.storage.api.StorageService;
import de.adorsys.datasafe_1_0_0.storage.impl.fs.FileSystemStorageService;
import de.adorsys.datasafe_1_0_0.storage.impl.s3.S3StorageService;
import de.adorsys.datasafe_1_0_0.types.api.utils.ExecutorServiceUtil;
import de.adorsys.datasafemigration.MigrationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.file.FileSystems;


@Slf4j
public class GetStorage {
    private static final String AMAZON_URL = "https://s3.amazonaws.com";
    private static final String S3_PREFIX = "s3://";

    public static SystemRootAndStorageService get(DFSCredentials dfsCredentials) {
        if (dfsCredentials instanceof FilesystemDFSCredentials) {
            return useFileSystem((FilesystemDFSCredentials) dfsCredentials);
        }
        if (dfsCredentials instanceof AmazonS3DFSCredentials) {
            return useAmazonS3((AmazonS3DFSCredentials) dfsCredentials);
        }
        throw new MigrationException("dont know to get storage of " + dfsCredentials.getClass().getName());
    }

    private static SystemRootAndStorageService useAmazonS3(AmazonS3DFSCredentials dfsCredentials) {
        AmazonS3DFSCredentials amazonS3DFSCredentials = dfsCredentials;
        AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        amazonS3DFSCredentials.getAccessKey(),
                                        amazonS3DFSCredentials.getSecretKey()))
                )
                .enablePathStyleAccess();

        boolean useEndpoint = !amazonS3DFSCredentials.getUrl().equals(AMAZON_URL)
                && !amazonS3DFSCredentials.getUrl().startsWith(S3_PREFIX);
        if (useEndpoint) {
            AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
                    amazonS3DFSCredentials.getUrl(),
                    amazonS3DFSCredentials.getRegion()
            );
            amazonS3ClientBuilder.withEndpointConfiguration(endpoint);
        } else {
            amazonS3ClientBuilder.withRegion(amazonS3DFSCredentials.getRegion());
        }

        if (amazonS3DFSCredentials.isNoHttps()) {
            log.info("Creating S3 client without https");
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setProtocol(Protocol.HTTP);
            clientConfig.disableSocketProxy();
            amazonS3ClientBuilder.withClientConfiguration(clientConfig);
        }

        AmazonS3 amazons3 = amazonS3ClientBuilder.build();

        if (!amazons3.doesBucketExistV2(amazonS3DFSCredentials.getContainer())) {
            amazons3.createBucket(amazonS3DFSCredentials.getContainer());
        }
        StorageService storageService = new S3StorageService(
                amazons3,
                amazonS3DFSCredentials.getContainer(),
                ExecutorServiceUtil
                        .submitterExecutesOnStarvationExecutingService(
                                amazonS3DFSCredentials.getThreadPoolSize(),
                                amazonS3DFSCredentials.getQueueSize()
                        )
        );
        URI systemRoot = URI.create(S3_PREFIX + amazonS3DFSCredentials.getRootBucket());
        log.info("build DFS to S3 with root " + amazonS3DFSCredentials.getRootBucket() + " and url " + amazonS3DFSCredentials.getUrl());
        return new SystemRootAndStorageService(systemRoot, storageService);
    }

    private static SystemRootAndStorageService useFileSystem(FilesystemDFSCredentials dfsCredentials) {
        FilesystemDFSCredentials filesystemDFSCredentials = dfsCredentials;
        URI systemRoot = FileSystems.getDefault().getPath(filesystemDFSCredentials.getRoot()).toAbsolutePath().toUri();
        StorageService storageService = new FileSystemStorageService(FileSystems.getDefault().getPath(filesystemDFSCredentials.getRoot()));
        log.info("build DFS to FILESYSTEM with root " + filesystemDFSCredentials.getRoot());
        return new SystemRootAndStorageService(systemRoot, storageService);
    }


    @AllArgsConstructor
    @Getter
    public static class SystemRootAndStorageService {
        private final URI systemRoot;
        private final StorageService storageService;
    }
}
