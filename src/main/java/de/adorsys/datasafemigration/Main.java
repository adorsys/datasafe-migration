package de.adorsys.datasafemigration;

import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.encryption.MutableEncryptionConfig;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_1.simple.adapter.impl.SimpleDatasafeServiceImpl;
import de.adorsys.datasafemigration.common.ReadUserPasswordFile;
import de.adorsys.datasafemigration.withDFSonly.LoadUserNewToNewFormat;
import de.adorsys.datasafemigration.withDFSonly.LoadUserOldToNewFormat;
import de.adorsys.datasafemigration.withDFSonly.WriteUserNewFormat;
import de.adorsys.datasafemigration.withlocalfilesystem.LoadNewUserToLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.LoadOldUserToLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteNewUserFromLocal;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteOldUserFromLocal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        DocumentDirectoryFQN localfolder = new DocumentDirectoryFQN(get(args, "localfolder"));
        List<UserIDAuth> usersToMigrate = ReadUserPasswordFile.getAllUsers(get(args, "userlistfile"));

        if (get(args, "action").equals("loadOldToLocal")) {
            de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService simpleDatasafeService = new de.adorsys.datasafe_0_6_1.simple.adapter.impl.SimpleDatasafeServiceImpl(getOldDfsCredentials(args));
            LoadOldUserToLocal service = new LoadOldUserToLocal(simpleDatasafeService, localfolder);
            for (UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, "action").equals("loadNewToLocal")) {
            SimpleDatasafeService simpleDatasafeService = new SimpleDatasafeServiceImpl(getNewDfsCredentials("new", args), new MutableEncryptionConfig());
            LoadNewUserToLocal service = new LoadNewUserToLocal(simpleDatasafeService, localfolder);
            for (UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, "action").equals("storeLocalToNew")) {
            SimpleDatasafeService simpleDatasafeService = new SimpleDatasafeServiceImpl(getNewDfsCredentials("new", args), new MutableEncryptionConfig());
            WriteNewUserFromLocal service = new WriteNewUserFromLocal(simpleDatasafeService, localfolder);
            for (UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, "action").equals("storeLocalToOld")) {
            de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService simpleDatasafeService = new de.adorsys.datasafe_0_6_1.simple.adapter.impl.SimpleDatasafeServiceImpl(getOldDfsCredentials(args));
            WriteOldUserFromLocal service = new WriteOldUserFromLocal(simpleDatasafeService, localfolder);
            for (UserIDAuth userIDAuth : usersToMigrate) {
                service.migrateUser(userIDAuth);
            }
        }
        if (get(args, "action").equals("migrateDFSToIntermediate")) {
            SimpleDatasafeService intermediateService = new SimpleDatasafeServiceImpl(getNewDfsCredentials("intermediate", args), new MutableEncryptionConfig());
            de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService oldService = new de.adorsys.datasafe_0_6_1.simple.adapter.impl.SimpleDatasafeServiceImpl(getOldDfsCredentials(args));

            WriteUserNewFormat intermediateWrite = new WriteUserNewFormat(intermediateService);
            LoadUserOldToNewFormat old = new LoadUserOldToNewFormat(oldService, intermediateWrite);

            for (UserIDAuth userIDAuth : usersToMigrate) {
                old.migrateUser(userIDAuth);
            }
        }
        if (get(args, "action").equals("migrateDFSFromIntermediate")) {
            SimpleDatasafeService intermediateService = new SimpleDatasafeServiceImpl(getNewDfsCredentials("intermediate", args), new MutableEncryptionConfig());
            SimpleDatasafeService newService = new SimpleDatasafeServiceImpl(getNewDfsCredentials("new", args), new MutableEncryptionConfig());
            WriteUserNewFormat newWrite = new WriteUserNewFormat(newService);
            LoadUserNewToNewFormat intermediateRead = new LoadUserNewToNewFormat(intermediateService, newWrite);

            for (UserIDAuth userIDAuth : usersToMigrate) {
                intermediateRead.migrateUser(userIDAuth);
            }
        }

    }

    private static de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DFSCredentials getOldDfsCredentials(String[] args) {
        String prefix = "old";
        return de.adorsys.datasafe_0_6_1.simple.adapter.api.types.AmazonS3DFSCredentials.builder()
                .accessKey(get(args, prefix + "-accesskey"))
                .secretKey(get(args, prefix + "-secretkey"))
                .rootBucket(get(args, prefix + "-rootbucket"))
                .url(get(args, prefix + "-url"))
                .noHttps(!get(args, prefix + "-url").startsWith("https"))
                .region(get(args, prefix + "-region"))
                .build();

    }

    private static DFSCredentials getNewDfsCredentials(String prefix, String[] args) {
        return AmazonS3DFSCredentials.builder()
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
