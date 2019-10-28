package de.adorsys.datasafemigration.loader;

import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceImpl;

public class Main {
    public static void main(String[] args) {
        SimpleDatasafeService simpleDatasafeService = new SimpleDatasafeServiceImpl(getDfsCredentials(args));
        UserIDAuth userIDAuth = getUserIDAuth(args);
        DocumentDirectoryFQN localfolder = new DocumentDirectoryFQN(get (args, "localfolder"));

        if (get(args, "action").equals("load")) {
            new LoadUser(simpleDatasafeService, userIDAuth, localfolder);
        }
        if (get(args, "action").equals("store")) {
            new WriteUser(simpleDatasafeService, userIDAuth, localfolder);
        }

    }

    private static UserIDAuth getUserIDAuth(String[] args) {
        return new UserIDAuth(get(args, "user"), get(args, "password"));
    }


    private static DFSCredentials getDfsCredentials(String[] args) {
        return AmazonS3DFSCredentials.builder()
                .accessKey(get(args, "accesskey"))
                .secretKey(get(args, "secretkey"))
                .rootBucket(get(args, "rootbucket"))
                .url(get(args, "url"))
                .noHttps(! get(args, "url").startsWith("https"))
                .region(get(args, "region"))
                .build();

    }

    private static String get(String[] args, String argToSearch) {
        for (String arg : args) {
            if (arg.startsWith(argToSearch + "=")) {
                return arg.substring(argToSearch.length()+1);
            }
        }
        throw new RuntimeException ("mandatory parameter " + argToSearch + " not found.");
    }
}
