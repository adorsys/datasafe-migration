package de.adorsys.datasafemigration.withDFSonly;

import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafemigration.common.SwitchVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@Slf4j
public class LoadUserOldToNewFormat {
    private final S061_SimpleDatasafeService sourceDatasafeService;
    private final S100_SimpleDatasafeService destDatasafeService;

    public MigrationInfo migrateUser(S100_UserIDAuth userIDAuth) {

        MigrationInfo migrationInfo = new MigrationInfo();
        Instant start = Instant.now();

        createUser(userIDAuth);

        List<S061_DocumentFQN> list = sourceDatasafeService.list(SwitchVersion.to_0_6_1(userIDAuth), new S061_DocumentDirectoryFQN("/"), S061_ListRecursiveFlag.TRUE);
        for (S061_DocumentFQN fqn : list) {
            S061_DSDocument dsDocument = sourceDatasafeService.readDocument(SwitchVersion.to_0_6_1(userIDAuth), fqn);
            storeDocument(userIDAuth, SwitchVersion.to_1_0_0(dsDocument));
            migrationInfo.incrementFiles();
            migrationInfo.addBytes(dsDocument.getDocumentContent().getValue().length);
        }
        migrationInfo.setDuration(Duration.between(start, Instant.now()));

        return migrationInfo;
    }

    private void createUser(S100_UserIDAuth userIDAuth) {
        if (destDatasafeService.userExists(userIDAuth.getUserID())) {
            throw new RuntimeException("user " + userIDAuth.getUserID().getValue() + " already exists");
        }

        destDatasafeService.createUser(userIDAuth);
        log.debug("created user {} in new format", userIDAuth.getUserID().getValue());
    }

    private void storeDocument(S100_UserIDAuth userIDAuth, S100_DSDocument dsDocument) {
        destDatasafeService.storeDocument(userIDAuth, dsDocument);
        log.debug("stored document of size {} in new format for user {}", dsDocument.getDocumentContent().getValue().length, userIDAuth.getUserID().getValue());
    }

    @Getter
    public static class MigrationInfo {
        Duration duration;
        Date startTime = new Date();
        long files = 0;
        long bytes = 0;
        public void incrementFiles() {
            files++;
        }
        public void addBytes(long number) {
            bytes += number;
        }
        public void setDuration(Duration d) {
            duration = d;
        }
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss,SSS");
            StringBuilder sb = new StringBuilder();
            sb.append("Migration started at ")
                    .append(sdf.format(startTime))
                    .append(" and took ")
                    .append(duration.toMillis())
                    .append(" milliseconds. ")
                    .append(files)
                    .append(" files with ")
                    .append(bytes)
                    .append(" bytes have been migrated.");
            return sb.toString();
        }
    }
}
