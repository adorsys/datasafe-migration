package de.adorsys.datasafemigration.common;


import de.adorsys.datasafe_0_7_0.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_0_7_0.simple.adapter.api.types.DocumentFQN;

public class SwitchVersion {
    public static de.adorsys.datasafe_0_6_1.encrypiton.api.types.UserIDAuth toOld(UserIDAuth newUserIDAuth) {
        return new de.adorsys.datasafe_0_6_1.encrypiton.api.types.UserIDAuth(
                new de.adorsys.datasafe_0_6_1.encrypiton.api.types.UserID(newUserIDAuth.getUserID().getValue()),
                new de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.ReadKeyPassword(new String(newUserIDAuth.getReadKeyPassword().getValue())));
    }

    public static DSDocument toNew(de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DSDocument oldDSDocument) {
        return new DSDocument(
                new DocumentFQN(oldDSDocument.getDocumentFQN().getDocusafePath()),
                new DocumentContent(oldDSDocument.getDocumentContent().getValue()));
    }

    public static de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentDirectoryFQN toOld(DocumentDirectoryFQN newDocumentDirectoryFQN) {
        return new de.adorsys.datasafe_0_6_1.simple.adapter.api.types.DocumentDirectoryFQN(newDocumentDirectoryFQN.getDocusafePath());
    }
}
