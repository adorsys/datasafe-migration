package de.adorsys.datasafemigration;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocumentStream;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.S061_ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_AmazonS3DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DSDocumentStream;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentContent;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_FilesystemDFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.S061_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocumentStream;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_3.types.api.types.S103_ReadKeyPassword;
import de.adorsys.datasafemigration.common.SwitchVersion;

public class ExtendedSwitchVersion extends SwitchVersion {

    public static S061_DSDocument to_0_6_1(S103_DSDocument dsDocument) {
        return new S061_DSDocument(
                new S061_DocumentFQN(dsDocument.getDocumentFQN().getDocusafePath()),
                new S061_DocumentContent(dsDocument.getDocumentContent().getValue())
        );
    }

    public static S061_DocumentFQN to_0_6_1(S103_DocumentFQN real) {
        return new S061_DocumentFQN(real.getDocusafePath());
    }

    public static S061_DocumentDirectoryFQN to_0_6_1(S103_DocumentDirectoryFQN real) {
        return new S061_DocumentDirectoryFQN(real.getDocusafePath());
    }

    public static S061_DSDocumentStream to_0_6_1(S103_DSDocumentStream real) {
        return new S061_DSDocumentStream(to_0_6_1(real.getDocumentFQN()), real.getDocumentStream());
    }


    public static S061_ReadKeyPassword to_0_6_1(S103_ReadKeyPassword real) {
        return new S061_ReadKeyPassword(new String(real.getValue()));
    }

    public static S061_DFSCredentials to_0_6_1(S103_DFSCredentials dfsCredentials) {
        if (dfsCredentials instanceof S103_AmazonS3DFSCredentials) {
            S103_AmazonS3DFSCredentials d = (S103_AmazonS3DFSCredentials) dfsCredentials;
            return S061_AmazonS3DFSCredentials.builder()
                    .rootBucket(d.getRootBucket())
                    .url(d.getUrl())
                    .accessKey(d.getAccessKey())
                    .secretKey(d.getSecretKey())
                    .noHttps(d.isNoHttps())
                    .region(d.getRegion())
                    .threadPoolSize(d.getThreadPoolSize())
                    .queueSize(d.getQueueSize()).build();
        }
        if (dfsCredentials instanceof S103_FilesystemDFSCredentials) {

            S103_FilesystemDFSCredentials d = (S103_FilesystemDFSCredentials) dfsCredentials;
            return S061_FilesystemDFSCredentials.builder()
                    .root(d.getRoot()).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }

    public static S061_ListRecursiveFlag to_0_6_1(ListRecursiveFlag listRecursiveFlag) {
        return listRecursiveFlag.equals(ListRecursiveFlag.TRUE) ?
                S061_ListRecursiveFlag.TRUE : S061_ListRecursiveFlag.FALSE;
    }



    public static S103_DFSCredentials to_1_0_3(S061_DFSCredentials dfsCredentials) {
        if (dfsCredentials instanceof S061_AmazonS3DFSCredentials) {
            S061_AmazonS3DFSCredentials d = (S061_AmazonS3DFSCredentials) dfsCredentials;
            return S103_AmazonS3DFSCredentials.builder()
                    .rootBucket(d.getRootBucket())
                    .url(d.getUrl())
                    .accessKey(d.getAccessKey())
                    .secretKey(d.getSecretKey())
                    .noHttps(d.isNoHttps())
                    .region(d.getRegion())
                    .threadPoolSize(d.getThreadPoolSize())
                    .queueSize(d.getQueueSize()).build();
        }
        if (dfsCredentials instanceof S061_FilesystemDFSCredentials) {

            S061_FilesystemDFSCredentials d = (S061_FilesystemDFSCredentials) dfsCredentials;
            return S103_FilesystemDFSCredentials.builder()
                    .root(d.getRoot()).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }

    public static S103_ListRecursiveFlag to_1_0_3(ListRecursiveFlag listRecursiveFlag) {
        return listRecursiveFlag.equals(ListRecursiveFlag.TRUE) ?
                S103_ListRecursiveFlag.TRUE : S103_ListRecursiveFlag.FALSE;
    }


    public static DSDocument toCurrent(S061_DSDocument readDocument) {
        return new DSDocument(
                new DocumentFQN(readDocument.getDocumentFQN().getDocusafePath()),
                new DocumentContent(readDocument.getDocumentContent().getValue())
        );
    }

    public static DSDocument toCurrent(S103_DSDocument readDocument) {
        return new DSDocument(
                new DocumentFQN(readDocument.getDocumentFQN().getDocusafePath()),
                new DocumentContent(readDocument.getDocumentContent().getValue())
        );
    }


    public static DSDocumentStream toCurrent(S103_DSDocumentStream readDocumentStream) {
        return new DSDocumentStream(
                new DocumentFQN(readDocumentStream.getDocumentFQN().getDocusafePath()),
                readDocumentStream.getDocumentStream());

    }

    public static DSDocumentStream toCurrent(S061_DSDocumentStream readDocumentStream) {
        return new DSDocumentStream(
                new DocumentFQN(readDocumentStream.getDocumentFQN().getDocusafePath()),
                readDocumentStream.getDocumentStream());
    }

    public static UserIDAuth toCurrent(S103_UserIDAuth userIDAuth) {
        return new UserIDAuth(
                new UserID(userIDAuth.getUserID().getValue()),
                new ReadKeyPassword(new String(userIDAuth.getReadKeyPassword().getValue())::toCharArray)
        );
    }

    public static DocumentFQN toCurrent(S103_DocumentFQN s100_documentFQN) {
        return new DocumentFQN(s100_documentFQN.getDocusafePath());
    }

    public static DocumentDirectoryFQN toCurrent(S103_DocumentDirectoryFQN s100_documentDirectoryFQN) {
        return new DocumentDirectoryFQN(s100_documentDirectoryFQN.getDocusafePath());
    }
}
