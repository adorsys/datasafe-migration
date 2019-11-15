package de.adorsys.datasafemigration.common;


import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserID;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.S061_ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentContent;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;

public class SwitchVersion {
    public static S061_UserIDAuth to_0_6_1(S100_UserIDAuth newUserIDAuth) {
        return new S061_UserIDAuth(
                new S061_UserID(newUserIDAuth.getUserID().getValue()),
                new S061_ReadKeyPassword(new String(newUserIDAuth.getReadKeyPassword().getValue())));
    }

    public static S100_DSDocument to_1_0_0(S061_DSDocument oldDSDocument) {
        return new S100_DSDocument(
                new S100_DocumentFQN(oldDSDocument.getDocumentFQN().getDocusafePath()),
                new S100_DocumentContent(oldDSDocument.getDocumentContent().getValue()));
    }

    public static S061_DocumentDirectoryFQN to_0_6_1(S100_DocumentDirectoryFQN newDocumentDirectoryFQN) {
        return new S061_DocumentDirectoryFQN(newDocumentDirectoryFQN.getDocusafePath());
    }
}
