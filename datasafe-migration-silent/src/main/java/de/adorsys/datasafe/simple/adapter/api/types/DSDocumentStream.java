package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocumentStream;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.InputStream;

@Getter
public class DSDocumentStream {
    @Delegate
    S103_DSDocumentStream real;

    public DSDocumentStream(DocumentFQN documentFQN, InputStream documentStream) {
        real = new S103_DSDocumentStream(documentFQN.getReal(), documentStream);
    }

}
