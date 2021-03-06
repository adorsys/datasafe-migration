package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.S103_SimpleDatasafeService;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_ListRecursiveFlag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadStructureUtil {
    static public Map<S103_UserIDAuth, Set<S103_DSDocument>> loadS100Structure(S103_SimpleDatasafeService s103_simpleDatasafeService, Set<S103_UserIDAuth> s103_userIDAuths) {

        Map<S103_UserIDAuth, Set<S103_DSDocument>> resultMap = new HashMap<>();

        for (S103_UserIDAuth s103_userIDAuth : s103_userIDAuths) {
            Set<S103_DSDocument> resultSet = new HashSet<>();
            List<S103_DocumentFQN> list = s103_simpleDatasafeService.list(s103_userIDAuth, new S103_DocumentDirectoryFQN("/"), S103_ListRecursiveFlag.TRUE);
            for (S103_DocumentFQN s103_documentFQN : list) {
                resultSet.add(s103_simpleDatasafeService.readDocument(s103_userIDAuth, s103_documentFQN));
            }
            resultMap.put(s103_userIDAuth, resultSet);
        }
        return resultMap;
    }

}
