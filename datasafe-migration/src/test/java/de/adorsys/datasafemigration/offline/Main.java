package de.adorsys.datasafemigration.offline;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_AmazonS3DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.impl.S061_SimpleDatasafeServiceImpl;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.impl.S100_SimpleDatasafeServiceImpl;
import de.adorsys.datasafemigration.withDFSonly.LoadUserNewToNewFormat;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import de.adorsys.datasafemigration.withlocalfilesystem.LoadNewUserToLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.LoadOldUserToLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteNewUserFromLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteOldUserFromLocal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.List;

public class Main {

    // mandatory parameters
    public static final String USERLISTFILE = "userlistfile";
    public static final String ACTION = "action";

    // actions with local filesystem
    public static final String LOAD_OLD_TO_LOCAL = "loadOldToLocal";
    public static final String LOAD_NEW_TO_LOCAL = "loadNewToLocal";
    public static final String STORE_LOCAL_TO_NEW = "storeLocalToNew";
    public static final String STORE_LOCAL_TO_OLD = "storeLocalToOld";

    // parameter for action with local filesyste
    public static final String LOCALFOLDER = "localfolder";

    // actions with dfs only
    public static final String MIGRATE_DFS_TO_INTERMEDIATE = "migrateDFSToIntermediate";
    public static final String MIGRATE_DFS_FROM_INTERMEDIATE = "migrateDFSFromIntermediate";

    // parameters for actions with dfs only
    public static final String OLD = "old";
    public static final String NEW = "new";
    public static final String ACCESSKEY = "-accesskey";
    public static final String SECRETKEY = "-secretkey";
    public static final String ROOTBUCKET = "-rootbucket";
    public static final String URL = "-url";
    public static final String REGION = "-region";

    public static final String HTTPS = "https";

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        List<S100_UserIDAuth> usersToMigrate = ReadUserPasswordFile.getAllUsers(get(args, USERLISTFILE));

        if (get(args, ACTION).equals(LOAD_OLD_TO_LOCAL)) {
            S100_DocumentDirectoryFQN localfolder = new S100_DocumentDirectoryFQN(get(args, LOCALFOLDER));
            S061_SimpleDatasafeService simpleDatasafeService = new S061_SimpleDatasafeServiceImpl(getOldDfsCredentials(args));
            LoadOldUserToLocal service = new LoadOldUserToLocal(simpleDatasafeService, localfolder);
            for (S100_UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, ACTION).equals(LOAD_NEW_TO_LOCAL)) {
            S100_DocumentDirectoryFQN localfolder = new S100_DocumentDirectoryFQN(get(args, LOCALFOLDER));
            S100_SimpleDatasafeService simpleDatasafeService = new S100_SimpleDatasafeServiceImpl(getNewDfsCredentials(NEW, args), new MutableEncryptionConfig());
            LoadNewUserToLocal service = new LoadNewUserToLocal(simpleDatasafeService, localfolder);
            for (S100_UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, ACTION).equals(STORE_LOCAL_TO_NEW)) {
            S100_DocumentDirectoryFQN localfolder = new S100_DocumentDirectoryFQN(get(args, LOCALFOLDER));
            S100_SimpleDatasafeService simpleDatasafeService = new S100_SimpleDatasafeServiceImpl(getNewDfsCredentials(NEW, args), new MutableEncryptionConfig());
            WriteNewUserFromLocal service = new WriteNewUserFromLocal(simpleDatasafeService, localfolder);
            for (S100_UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, ACTION).equals(STORE_LOCAL_TO_OLD)) {
            S100_DocumentDirectoryFQN localfolder = new S100_DocumentDirectoryFQN(get(args, LOCALFOLDER));
            S061_SimpleDatasafeService simpleDatasafeService = new S061_SimpleDatasafeServiceImpl(getOldDfsCredentials(args));
            WriteOldUserFromLocal service = new WriteOldUserFromLocal(simpleDatasafeService, localfolder);
            for (S100_UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, ACTION).equals(MIGRATE_DFS_TO_INTERMEDIATE)) {
            S100_SimpleDatasafeService intermediateService = new S100_SimpleDatasafeServiceImpl(getNewDfsCredentials("intermediate", args), new MutableEncryptionConfig());
            S061_SimpleDatasafeService oldService = new S061_SimpleDatasafeServiceImpl(getOldDfsCredentials(args));

            LoadUserOldToNewFormat old = new LoadUserOldToNewFormat(oldService, intermediateService);

            for (S100_UserIDAuth userIDAuth : usersToMigrate) {
                old.migrateUser(userIDAuth);
            }
        }
        if (get(args, ACTION).equals(MIGRATE_DFS_FROM_INTERMEDIATE)) {
            S100_SimpleDatasafeService intermediateService = new S100_SimpleDatasafeServiceImpl(getNewDfsCredentials("intermediate", args), new MutableEncryptionConfig());
            S100_SimpleDatasafeService newService = new S100_SimpleDatasafeServiceImpl(getNewDfsCredentials(NEW, args), new MutableEncryptionConfig());
            LoadUserNewToNewFormat intermediateRead = new LoadUserNewToNewFormat(intermediateService, newService);

            for (S100_UserIDAuth userIDAuth : usersToMigrate) {
                intermediateRead.migrateUser(userIDAuth);
            }
        }

    }

    private static S061_DFSCredentials getOldDfsCredentials(String[] args) {
        String prefix = OLD;
        return S061_AmazonS3DFSCredentials.builder()
                .accessKey(get(args, prefix + ACCESSKEY))
                .secretKey(get(args, prefix + SECRETKEY))
                .rootBucket(get(args, prefix + ROOTBUCKET))
                .url(get(args, prefix + URL))
                .noHttps(!get(args, prefix + URL).startsWith(HTTPS))
                .region(get(args, prefix + REGION))
                .build();

    }

    private static S100_DFSCredentials getNewDfsCredentials(String prefix, String[] args) {
        return S100_AmazonS3DFSCredentials.builder()
                .accessKey(get(args, prefix + "-accesskey"))
                .secretKey(get(args, prefix + "-secretkey"))
                .rootBucket(get(args, prefix + "-rootbucket"))
                .url(get(args, prefix + "-url"))
                .noHttps(!get(args, prefix + "-url").startsWith("https"))
                .region(get(args, prefix + "-region"))
                .build();
    }

    private static String get(String[] args, String argToSearch) {
        for (String arg : args) {
            if (arg.startsWith(argToSearch + "=")) {
                return arg.substring(argToSearch.length() + 1);
            }
        }
        throw new RuntimeException("mandatory parameter " + argToSearch + " not found.");
    }
}
