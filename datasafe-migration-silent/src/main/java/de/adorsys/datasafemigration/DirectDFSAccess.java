package de.adorsys.datasafemigration;

import com.amazonaws.util.IOUtils;
import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.impl.GetStorage;
import de.adorsys.datasafe_1_0_0.types.api.resource.AbsoluteLocation;
import de.adorsys.datasafe_1_0_0.types.api.resource.BasePrivateResource;
import de.adorsys.datasafe_1_0_0.types.api.resource.PrivateResource;
import de.adorsys.datasafe_1_0_0.types.api.resource.ResolvedResource;
import de.adorsys.datasafe_1_0_0.types.api.resource.WithCallback;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Slf4j
public class DirectDFSAccess {
    @SneakyThrows
    static public void storeFileInUsersRootDir(GetStorage.SystemRootAndStorageService storage, UserID userID, DSDocument document) {
        String root = getSystemRootDirOfUsers(storage.getSystemRoot(), userID);
        String wholePath = root + document.getDocumentFQN().getDatasafePath();
        log.info("absolute location {}", wholePath);
        AbsoluteLocation<PrivateResource> abso = BasePrivateResource.forAbsolutePrivate(wholePath);
        try (OutputStream write = storage.getStorageService().write(WithCallback.noCallback(abso))) {
            write.write(document.getDocumentContent().getValue());
        }
    }

    static public MoveInfo moveAllFiles(GetStorage.SystemRootAndStorageService source, GetStorage.SystemRootAndStorageService dest, UserID userID) {
        MoveInfo mi = new MoveInfo();
        Instant start = Instant.now();

        String sourceRoot = getSystemRootDirOfUsers(source.getSystemRoot(), userID);
        AbsoluteLocation<PrivateResource> sourceLocation = BasePrivateResource.forAbsolutePrivate(sourceRoot);
        try (Stream<AbsoluteLocation<ResolvedResource>> list = source.getStorageService().list(sourceLocation)) {
            list.forEach(el -> {
                mi.increaseFiles();
                String elString = el.location().asURI().toString();
                if (!elString.startsWith(sourceRoot)) {
                    throw new MigrationException("expected path "+ elString + " to start with "+ sourceRoot);
                }
                String relativePath = elString.substring(sourceRoot.length());
                mi.increaseBytes(moveSingleFile(source, dest, userID, relativePath));
            });
        }
        mi.setDuration(Duration.between(start, Instant.now()));
        return mi;
    }

    @Getter
    @NoArgsConstructor
    public static class MoveInfo {
        long bytes = 0;
        long numberOfFiles = 0;
        Duration duration = null;
        public void increaseFiles() {
            numberOfFiles++;
        }
        public void increaseBytes(long inc) {
            bytes += inc;
        }
        public void setDuration(Duration d) {
            duration = d;
        }
        public String toString() {
            return "moved " + numberOfFiles + " with in total " + bytes + " in " + duration.toMillis() + " milliseconds";
        }
    }

    @SneakyThrows
    static private long moveSingleFile(GetStorage.SystemRootAndStorageService source, GetStorage.SystemRootAndStorageService dest, UserID userID, String relativePath) {
        String sourceRoot = getSystemRootDirOfUsers(source.getSystemRoot(), userID);
        String sourcePath = sourceRoot + relativePath;
        AbsoluteLocation<PrivateResource> sourceLocation = BasePrivateResource.forAbsolutePrivate(sourcePath);

        String destRoot = getSystemRootDirOfUsers(dest.getSystemRoot(), userID);
        String destPath = destRoot + relativePath;
        AbsoluteLocation<PrivateResource> destLocation = BasePrivateResource.forAbsolutePrivate(destPath);

        log.debug("copy from {}",sourcePath);
        log.debug("copy   to {}",destPath);
        try (InputStream read = source.getStorageService().read(sourceLocation)) {
            try (OutputStream write = dest.getStorageService().write(WithCallback.noCallback(destLocation))) {
                return IOUtils.copy(read, write);
            }
        }
    }

    static private String getSystemRootDirOfUsers(URI systemRoot, UserID userID) {
        String root = systemRoot.toString();
        if (! root.endsWith("/")) {
            root = root + "/";
        }
        return root + "users/" + userID.getValue() + "/";
    }

}
