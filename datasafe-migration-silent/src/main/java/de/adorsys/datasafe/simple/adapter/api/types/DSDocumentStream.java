package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocumentStream;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.InputStream;

@Getter
public class DSDocumentStream {
    @Delegate
    S100_DSDocumentStream real;

    public DSDocumentStream(DocumentFQN documentFQN, InputStream documentStream) {
        real = new S100_DSDocumentStream(documentFQN.getReal(), documentStream);
    }

}
