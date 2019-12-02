package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserIDAuth;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.S101_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocument;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_ListRecursiveFlag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadStructureUtil {
    static public Map<S101_UserIDAuth, Set<S101_DSDocument>> loadS100Structure(S101_SimpleDatasafeService s100_simpleDatasafeService, Set<S101_UserIDAuth> s100_userIDAuths) {

        Map<S101_UserIDAuth, Set<S101_DSDocument>> resultMap = new HashMap<>();

        for (S101_UserIDAuth s100_userIDAuth : s100_userIDAuths) {
            Set<S101_DSDocument> resultSet = new HashSet<>();
            List<S101_DocumentFQN> list = s100_simpleDatasafeService.list(s100_userIDAuth, new S101_DocumentDirectoryFQN("/"), S101_ListRecursiveFlag.TRUE);
            for (S101_DocumentFQN s100_documentFQN : list) {
                resultSet.add(s100_simpleDatasafeService.readDocument(s100_userIDAuth, s100_documentFQN));
            }
            resultMap.put(s100_userIDAuth, resultSet);
        }
        return resultMap;
    }

}
