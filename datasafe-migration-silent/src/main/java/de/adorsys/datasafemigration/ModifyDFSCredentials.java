package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_FilesystemDFSCredentials;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModifyDFSCredentials {
    public static String DEFAULT_NEW_PATH_SUFFIX = "103/";
    private static String BEST_CASE_SUFFIX = "datasafe/backend/";
    private static String BEST_CASE_NEW_SUFFIX = "datasafe/" + DEFAULT_NEW_PATH_SUFFIX + "backend/";

    public static S103_DFSCredentials appendToRootPath(S103_DFSCredentials dfsCredentials, String pathToAppend) {
        String currentRoot = getCurrentRootPath(dfsCredentials);
        if (!(currentRoot.endsWith("/"))) {
            currentRoot = currentRoot + "/";
        }
        if (pathToAppend.startsWith("/")) {
            pathToAppend = pathToAppend.substring(1);
        }
        if (!pathToAppend.endsWith("/")) {
            pathToAppend = pathToAppend + "/";
        }

        String newRootPath = currentRoot + pathToAppend;
        return changeRootpath(dfsCredentials, newRootPath);
    }

    public static S103_DFSCredentials getPathToMigratedData(S103_DFSCredentials dfsCredentials) {
        return changeRootpath(dfsCredentials, getModifiedRootPath(getCurrentRootPath(dfsCredentials)));
    }

    public static String getCurrentRootPath(S103_DFSCredentials dfsCredentials) {
        String currentRoot = null;
        if (dfsCredentials instanceof S103_AmazonS3DFSCredentials) {
            S103_AmazonS3DFSCredentials d = (S103_AmazonS3DFSCredentials) dfsCredentials;
            currentRoot = d.getRootBucket();
        }
        if (dfsCredentials instanceof S103_FilesystemDFSCredentials) {
            S103_FilesystemDFSCredentials d = (S103_FilesystemDFSCredentials) dfsCredentials;
            currentRoot = d.getRoot();
        }
        if (currentRoot == null) {
            throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
        }
        return currentRoot;
    }

    private static String getModifiedRootPath(String currentRoot) {
        if (!(currentRoot.endsWith("/"))) {
            currentRoot = currentRoot + "/";
        }

        // bestcase
        // s3://adorsys-timp-dev/datasafe/backend/

        log.debug("oldRootPath is {}", currentRoot);
        if (currentRoot.endsWith(BEST_CASE_SUFFIX)) {
            String prefix = currentRoot.substring(0, currentRoot.length() - BEST_CASE_SUFFIX.length());
            return prefix + BEST_CASE_NEW_SUFFIX;
        }

        if (currentRoot.endsWith(DEFAULT_NEW_PATH_SUFFIX)) {
            throw new MigrationException("DFS Credentials NOT migrated data root bucket is " + currentRoot + ".");
        }

        return currentRoot + DEFAULT_NEW_PATH_SUFFIX;
    }

    private static S103_DFSCredentials changeRootpath(S103_DFSCredentials dfsCredentials, String newRootPath) {
        if (dfsCredentials instanceof S103_AmazonS3DFSCredentials) {
            S103_AmazonS3DFSCredentials d = (S103_AmazonS3DFSCredentials) dfsCredentials;
            return S103_AmazonS3DFSCredentials.builder()
                .rootBucket(newRootPath)
                .url(d.getUrl())
                .accessKey(d.getAccessKey())
                .secretKey(d.getSecretKey())
                .noHttps(d.isNoHttps())
                .region(d.getRegion())
                .threadPoolSize(d.getThreadPoolSize())
                .queueSize(d.getQueueSize()).build();
        }
        if (dfsCredentials instanceof S103_FilesystemDFSCredentials) {

            S103_FilesystemDFSCredentials d = (S103_FilesystemDFSCredentials) dfsCredentials;
            return S103_FilesystemDFSCredentials.builder()
                .root(newRootPath).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }


}
