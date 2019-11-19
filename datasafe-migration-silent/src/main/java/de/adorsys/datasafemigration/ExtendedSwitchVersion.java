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
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocumentStream;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_ListRecursiveFlag;
import de.adorsys.datasafe_1_0_0.types.api.types.S100_ReadKeyPassword;
import de.adorsys.datasafemigration.common.SwitchVersion;

public class ExtendedSwitchVersion extends SwitchVersion {

    public static S061_DSDocument to_0_6_1(S100_DSDocument dsDocument) {
        return new S061_DSDocument(
                new S061_DocumentFQN(dsDocument.getDocumentFQN().getDocusafePath()),
                new S061_DocumentContent(dsDocument.getDocumentContent().getValue())
        );
    }

    public static S061_DocumentFQN to_0_6_1(S100_DocumentFQN real) {
        return new S061_DocumentFQN(real.getDocusafePath());
    }

    public static S061_DocumentDirectoryFQN to_0_6_1(S100_DocumentDirectoryFQN real) {
        return new S061_DocumentDirectoryFQN(real.getDocusafePath());
    }

    public static S061_DSDocumentStream to_0_6_1(S100_DSDocumentStream real) {
        return new S061_DSDocumentStream(to_0_6_1(real.getDocumentFQN()), real.getDocumentStream());
    }


    public static S061_ReadKeyPassword to_0_6_1(S100_ReadKeyPassword real) {
        return new S061_ReadKeyPassword(new String(real.getValue()));
    }

    public static S061_DFSCredentials to_0_6_1(S100_DFSCredentials dfsCredentials) {
        if (dfsCredentials instanceof S100_AmazonS3DFSCredentials) {
            S100_AmazonS3DFSCredentials d = (S100_AmazonS3DFSCredentials) dfsCredentials;
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
        if (dfsCredentials instanceof S100_FilesystemDFSCredentials) {

            S100_FilesystemDFSCredentials d = (S100_FilesystemDFSCredentials) dfsCredentials;
            return S061_FilesystemDFSCredentials.builder()
                    .root(d.getRoot()).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }

    public static S100_DFSCredentials to_1_0_0(S061_DFSCredentials dfsCredentials) {
        if (dfsCredentials instanceof S061_AmazonS3DFSCredentials) {
            S061_AmazonS3DFSCredentials d = (S061_AmazonS3DFSCredentials) dfsCredentials;
            return S100_AmazonS3DFSCredentials.builder()
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
            return S100_FilesystemDFSCredentials.builder()
                    .root(d.getRoot()).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }

    public static S100_ListRecursiveFlag to_1_0_0(ListRecursiveFlag listRecursiveFlag) {
        return listRecursiveFlag.equals(ListRecursiveFlag.TRUE) ?
                S100_ListRecursiveFlag.TRUE : S100_ListRecursiveFlag.FALSE;
    }


    public static DSDocument toCurrent(S061_DSDocument readDocument) {
        return new DSDocument(
                new DocumentFQN(readDocument.getDocumentFQN().getDocusafePath()),
                new DocumentContent(readDocument.getDocumentContent().getValue())
        );
    }

    public static DSDocument toCurrent(S100_DSDocument readDocument) {
        return new DSDocument(
                new DocumentFQN(readDocument.getDocumentFQN().getDocusafePath()),
                new DocumentContent(readDocument.getDocumentContent().getValue())
        );
    }


    public static DSDocumentStream toCurrent(S100_DSDocumentStream readDocumentStream) {
        return new DSDocumentStream(
                new DocumentFQN(readDocumentStream.getDocumentFQN().getDocusafePath()),
                readDocumentStream.getDocumentStream());

    }

    public static DSDocumentStream toCurrent(S061_DSDocumentStream readDocumentStream) {
        return new DSDocumentStream(
                new DocumentFQN(readDocumentStream.getDocumentFQN().getDocusafePath()),
                readDocumentStream.getDocumentStream());
    }

    public static UserIDAuth toCurrent(S100_UserIDAuth userIDAuth) {
        return new UserIDAuth(
                new UserID(userIDAuth.getUserID().getValue()),
                new ReadKeyPassword(new String(userIDAuth.getReadKeyPassword().getValue())::toCharArray)
        );
    }

    public static DocumentFQN toCurrent(S100_DocumentFQN s100_documentFQN) {
        return new DocumentFQN(s100_documentFQN.getDocusafePath());
    }

    public static DocumentDirectoryFQN toCurrent(S100_DocumentDirectoryFQN s100_documentDirectoryFQN) {
        return new DocumentDirectoryFQN(s100_documentDirectoryFQN.getDocusafePath());
    }
}
