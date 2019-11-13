package de.adorsys.datasafe.datasafemigration;

import de.adorsys.datasafe_0_6_1.encrypiton.api.types.keystore.SO_ReadKeyPassword;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_AmazonS3DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DFSCredentials;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DSDocument;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DSDocumentStream;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentContent;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentDirectoryFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_DocumentFQN;
import de.adorsys.datasafe_0_6_1.simple.adapter.api.types.SO_FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocumentStream;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.FilesystemDFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.ListRecursiveFlag;
import de.adorsys.datasafe_1_0_0.types.api.types.ReadKeyPassword;
import de.adorsys.datasafemigration.common.SwitchVersion;

public class ExtendedSwitchVersion extends SwitchVersion {

    public static SO_DSDocument toOld(DSDocument dsDocument) {
        return new SO_DSDocument(
                new SO_DocumentFQN(dsDocument.getDocumentFQN().getDocusafePath()),
                new SO_DocumentContent(dsDocument.getDocumentContent().getValue())
        );
    }

    public static SO_DocumentFQN toOld(DocumentFQN real) {
        return new SO_DocumentFQN(real.getDocusafePath());
    }

    public static SO_DocumentDirectoryFQN toOld(DocumentDirectoryFQN real) {
        return new SO_DocumentDirectoryFQN(real.getDocusafePath());
    }

    public static de.adorsys.datasafe.simple.adapter.api.types.DSDocument toCurrentnewReal(SO_DSDocument readDocument) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DSDocument(
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(readDocument.getDocumentFQN().getDocusafePath()),
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentContent(readDocument.getDocumentContent().getValue())
        );
    }
    public static de.adorsys.datasafe.simple.adapter.api.types.DSDocument toCurrentnewReal(DSDocument readDocument) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DSDocument(
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(readDocument.getDocumentFQN().getDocusafePath()),
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentContent(readDocument.getDocumentContent().getValue())
        );
    }


    public static de.adorsys.datasafe.simple.adapter.api.types.DSDocumentStream toCurrentnewReal(DSDocumentStream readDocumentStream) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DSDocumentStream (
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(readDocumentStream.getDocumentFQN().getDocusafePath()),
                readDocumentStream.getDocumentStream());

    }

    public static de.adorsys.datasafe.simple.adapter.api.types.DSDocumentStream toCurrentnewReal(SO_DSDocumentStream readDocumentStream) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DSDocumentStream (
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(readDocumentStream.getDocumentFQN().getDocusafePath()),
                readDocumentStream.getDocumentStream());
    }

    public static SO_DSDocumentStream toOld(DSDocumentStream real) {
        return new SO_DSDocumentStream(toOld(real.getDocumentFQN()), real.getDocumentStream());
    }

    public static ListRecursiveFlag toNew(de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag listRecursiveFlag) {
        return listRecursiveFlag.equals(de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag.TRUE) ?
                ListRecursiveFlag.TRUE : ListRecursiveFlag.FALSE;
    }

    public static SO_ReadKeyPassword toOld(ReadKeyPassword real) {
        return new SO_ReadKeyPassword(new String(real.getValue()));
    }

    public static SO_DFSCredentials toOld(DFSCredentials dfsCredentials) {
        if (dfsCredentials instanceof AmazonS3DFSCredentials) {
            AmazonS3DFSCredentials d = (AmazonS3DFSCredentials) dfsCredentials;
            return SO_AmazonS3DFSCredentials.builder()
                    .rootBucket(d.getRootBucket())
                    .url(d.getUrl())
                    .accessKey(d.getAccessKey())
                    .secretKey(d.getSecretKey())
                    .noHttps(d.isNoHttps())
                    .region(d.getRegion())
                    .threadPoolSize(d.getThreadPoolSize())
                    .queueSize(d.getQueueSize()).build();
            }
        if (dfsCredentials instanceof FilesystemDFSCredentials) {

            FilesystemDFSCredentials d = (FilesystemDFSCredentials) dfsCredentials;
            return SO_FilesystemDFSCredentials.builder()
                    .root(d.getRoot()).build();

        }
        throw new RuntimeException("DFSCredentials have new class not known to the code: " + dfsCredentials.getClass().toString());
    }
}
