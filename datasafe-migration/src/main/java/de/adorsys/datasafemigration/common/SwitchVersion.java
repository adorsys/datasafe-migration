package de.adorsys.datasafemigration.common;


import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserID;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.S061_UserIDAuth;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.S061_ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserID;
import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserIDAuth;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocument;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentContent;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentFQN;
import de.adorsys.datasafe_1_0_1.types.api.types.S101_ReadKeyPassword;

public class SwitchVersion {
    public static S061_UserIDAuth to_0_6_1(S101_UserIDAuth newUserIDAuth) {
        return new S061_UserIDAuth(
                to_0_6_1(newUserIDAuth.getUserID()),
                new S061_ReadKeyPassword(new String(newUserIDAuth.getReadKeyPassword().getValue())));
    }

    public static S061_UserID to_0_6_1(S101_UserID newUserID) {
        return new S061_UserID(newUserID.getValue());
    }

    public static S101_DSDocument to_1_0_1(S061_DSDocument oldDSDocument) {
        return new S101_DSDocument(
                to_1_0_1(oldDSDocument.getDocumentFQN()),
                new S101_DocumentContent(oldDSDocument.getDocumentContent().getValue()));
    }

    public static S061_DocumentDirectoryFQN to_0_6_1(S101_DocumentDirectoryFQN newDocumentDirectoryFQN) {
        return new S061_DocumentDirectoryFQN(newDocumentDirectoryFQN.getDocusafePath());
    }

    public static S061_DocumentFQN to_0_6_1(S101_DocumentFQN newDocumentFQN) {
        return new S061_DocumentFQN(newDocumentFQN.getDocusafePath());
    }

    public static S101_UserIDAuth to_1_0_1(S061_UserIDAuth s061_userIDAuth) {
        return new S101_UserIDAuth(
                new S101_UserID(s061_userIDAuth.getUserID().getValue()),
                new S101_ReadKeyPassword(s061_userIDAuth.getReadKeyPassword().getValue()::toCharArray));
    }

    public static S101_DocumentFQN to_1_0_1(S061_DocumentFQN s061_documentFQN) {
        return new S101_DocumentFQN(s061_documentFQN.getDocusafePath());
    }
}
