package de.adorsys.datasafemigration.common;


import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserID;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.S061_ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserID;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentContent;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import de.adorsys.datasafe_1_0_3.types.api.types.S103_ReadKeyPassword;

public class SwitchVersion {
    public static S061_UserIDAuth to_0_6_1(S103_UserIDAuth newUserIDAuth) {
        return new S061_UserIDAuth(
                to_0_6_1(newUserIDAuth.getUserID()),
                new S061_ReadKeyPassword(new String(newUserIDAuth.getReadKeyPassword().getValue())));
    }

    public static S061_UserID to_0_6_1(S103_UserID newUserID) {
        return new S061_UserID(newUserID.getValue());
    }

    public static S103_DSDocument to_1_0_3(S061_DSDocument oldDSDocument) {
        return new S103_DSDocument(
                to_1_0_3(oldDSDocument.getDocumentFQN()),
                new S103_DocumentContent(oldDSDocument.getDocumentContent().getValue()));
    }

    public static S061_DocumentDirectoryFQN to_0_6_1(S103_DocumentDirectoryFQN newDocumentDirectoryFQN) {
        return new S061_DocumentDirectoryFQN(newDocumentDirectoryFQN.getDocusafePath());
    }

    public static S061_DocumentFQN to_0_6_1(S103_DocumentFQN newDocumentFQN) {
        return new S061_DocumentFQN(newDocumentFQN.getDocusafePath());
    }

    public static S103_UserIDAuth to_1_0_3(S061_UserIDAuth s061_userIDAuth) {
        return new S103_UserIDAuth(
                new S103_UserID(s061_userIDAuth.getUserID().getValue()),
                new S103_ReadKeyPassword(s061_userIDAuth.getReadKeyPassword().getValue()::toCharArray));
    }

    public static S103_DocumentFQN to_1_0_3(S061_DocumentFQN s061_documentFQN) {
        return new S103_DocumentFQN(s061_documentFQN.getDocusafePath());
    }
}
