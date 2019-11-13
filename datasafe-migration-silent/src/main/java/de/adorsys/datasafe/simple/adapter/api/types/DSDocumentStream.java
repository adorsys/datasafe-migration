package de.adorsys.datasafe.simple.adapter.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.InputStream;

@Getter
public class DSDocumentStream {
    @Delegate
    de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocumentStream real;

    public DSDocumentStream(DocumentFQN documentFQN, InputStream documentStream) {
        real = new de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocumentStream(documentFQN.getReal(), documentStream);
    }

}
