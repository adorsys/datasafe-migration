package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_FilesystemDFSCredentials;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModifyDFSCredentials {
    private static String BEST_CASE_SUFFIX = "datasafe/backend/";
    private static String BEST_CASE_NEW_SUFFIX = "datasafe/100/backend/";
    private static String DEFAULT_NEW_PATH_SUFFIX = "100/";

    public static S100_DFSCredentials appendToRootPath(S100_DFSCredentials dfsCredentials, String pathToAppend) {
        String currentRoot = getCurrentRootPath(dfsCredentials);
        if (! (currentRoot.endsWith("/"))) {
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

    public static S100_DFSCredentials getPathToMigratedData(S100_DFSCredentials dfsCredentials) {
        return changeRootpath(dfsCredentials, getModifiedRootPath(getCurrentRootPath(dfsCredentials)));
    }

    public static String getCurrentRootPath(S100_DFSCredentials dfsCredentials) {
        String currentRoot = null;
        if (dfsCredentials instanceof S100_AmazonS3DFSCredentials) {
            S100_AmazonS3DFSCredentials d = (S100_AmazonS3DFSCredentials) dfsCredentials;
            currentRoot = d.getRootBucket();
        }
        if (dfsCredentials instanceof S100_FilesystemDFSCredentials) {
            S100_FilesystemDFSCredentials d = (S100_FilesystemDFSCredentials) dfsCredentials;
            currentRoot = d.getRoot();
        }
        if (currentRoot == null) {
            throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
        }
        return currentRoot;
    }

    private static String getModifiedRootPath(String currentRoot) {
        if (! (currentRoot.endsWith("/"))) {
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

    private static S100_DFSCredentials changeRootpath(S100_DFSCredentials dfsCredentials, String newRootPath) {
        if (dfsCredentials instanceof S100_AmazonS3DFSCredentials) {
            S100_AmazonS3DFSCredentials d = (S100_AmazonS3DFSCredentials) dfsCredentials;
            return S100_AmazonS3DFSCredentials.builder()
                    .rootBucket(newRootPath)
                    .url(d.getUrl())
                    .accessKey(d.getAccessKey())
                    .secretKey(d.getSecretKey())
                    .noHttps(d.isNoHttps())
                    .region(d.getRegion())
                    .threadPoolSize(d.getThreadPoolSize())
                    .queueSize(d.getQueueSize()).build();
        }
        if (dfsCredentials instanceof S100_FilesystemDFSCredentials) {

            S100_FilesystemDFSCredentials d = (S100_FilesystemDFSCredentials) dfsCredentials;
            return S100_FilesystemDFSCredentials.builder()
                    .root(newRootPath).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }



}
