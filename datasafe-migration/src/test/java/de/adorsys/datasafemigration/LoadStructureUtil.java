package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.S100_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_ListRecursiveFlag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadStructureUtil {
    static public Map<S100_UserIDAuth, Set<S100_DSDocument>> loadS100Structure(S100_SimpleDatasafeService s100_simpleDatasafeService, Set<S100_UserIDAuth> s100_userIDAuths) {

        Map<S100_UserIDAuth, Set<S100_DSDocument>> resultMap = new HashMap<>();

        for (S100_UserIDAuth s100_userIDAuth : s100_userIDAuths) {
            Set<S100_DSDocument> resultSet = new HashSet<>();
            List<S100_DocumentFQN> list = s100_simpleDatasafeService.list(s100_userIDAuth, new S100_DocumentDirectoryFQN("/"), S100_ListRecursiveFlag.TRUE);
            for (S100_DocumentFQN s100_documentFQN : list) {
                resultSet.add(s100_simpleDatasafeService.readDocument(s100_userIDAuth, s100_documentFQN));
            }
            resultMap.put(s100_userIDAuth, resultSet);
        }
        return resultMap;
    }

}
