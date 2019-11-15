package de.adorsys.datasafemigration;

import com.googlecode.cqengine.query.simple.Has;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.S061_SimpleDatasafeService;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentContent;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafemigration.withlocalfilesystem.WriteOldUserFromLocal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Slf4j
public class CreateStructureUtil {
    static public Map<S061_UserIDAuth, Set<S061_DSDocument>> create061Structure(S061_SimpleDatasafeService s061_simpleDatasafeService, Set<S061_UserIDAuth> s061_userIDAuths) {

        Map<S061_UserIDAuth, Set<S061_DSDocument>> resultMap = new HashMap<>();

        for (S061_UserIDAuth s061_userIDAuth : s061_userIDAuths) {
            S061_DocumentDirectoryFQN startDir = new S061_DocumentDirectoryFQN("/");

            Set<S061_DSDocument> docs = new HashSet<>();
            createLocalFilesInMemory(docs, startDir, 3, 3, 2, 1000);

            s061_simpleDatasafeService.createUser(s061_userIDAuth);
            docs.forEach(doc -> s061_simpleDatasafeService.storeDocument(s061_userIDAuth, doc));

            resultMap.put(s061_userIDAuth, docs);
        }
        return resultMap;
    }

    @SneakyThrows
    private static void createLocalFilesInMemory(Set<S061_DSDocument> docs, S061_DocumentDirectoryFQN path, int recursiveDepth, int numberOfFiles, int numberOfSubdirs, int sizeOfFile) {
        if (recursiveDepth == 0) {
            return;
        }

        for (int i = 0; i < numberOfFiles; i++) {
            docs.add(new S061_DSDocument(path.addName("File_" + i), createS061DocumentContent(sizeOfFile)));
        }

        for (int i = 0; i < numberOfSubdirs; i++) {
            createLocalFilesInMemory(docs, path.addDirectory("SUBDIR_" + i), recursiveDepth - 1, numberOfFiles, numberOfSubdirs, sizeOfFile);
        }
    }

    private static S061_DocumentContent createS061DocumentContent(int sizeOfDocument) {
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);
        return new S061_DocumentContent(bytes);
    }


}
