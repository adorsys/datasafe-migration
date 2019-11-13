package de.adorsys.datasafe.simple.adapter.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DSDocument {
    @Delegate
    de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocument real;

    public DSDocument(DocumentFQN documentFQN, DocumentContent documentContent) {
        real = new de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DSDocument(
                documentFQN.getReal(),
                documentContent.getReal()
        );
    }

}
