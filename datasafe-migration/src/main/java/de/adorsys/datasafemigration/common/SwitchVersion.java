package de.adorsys.datasafemigration.common;


import de.adorsys.datasafe_0_6_1.encrypiton.api.types.SO_UserID;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.SO_UserIDAuth;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.SO_ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentFQN;

public class SwitchVersion {
    public static SO_UserIDAuth toOld(UserIDAuth newUserIDAuth) {
        return new SO_UserIDAuth(
                new SO_UserID(newUserIDAuth.getUserID().getValue()),
                new SO_ReadKeyPassword(new String(newUserIDAuth.getReadKeyPassword().getValue())));
    }

    public static DSDocument toNew(SO_DSDocument oldDSDocument) {
        return new DSDocument(
                new DocumentFQN(oldDSDocument.getDocumentFQN().getDocusafePath()),
                new DocumentContent(oldDSDocument.getDocumentContent().getValue()));
    }

    public static SO_DocumentDirectoryFQN toOld(DocumentDirectoryFQN newDocumentDirectoryFQN) {
        return new SO_DocumentDirectoryFQN(newDocumentDirectoryFQN.getDocusafePath());
    }
}
