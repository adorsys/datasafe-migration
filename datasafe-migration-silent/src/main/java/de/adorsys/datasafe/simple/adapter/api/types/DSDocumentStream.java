package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocumentStream;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.InputStream;

@Getter
public class DSDocumentStream {
    @Delegate
    S101_DSDocumentStream real;

    public DSDocumentStream(DocumentFQN documentFQN, InputStream documentStream) {
        real = new S101_DSDocumentStream(documentFQN.getReal(), documentStream);
    }

}
