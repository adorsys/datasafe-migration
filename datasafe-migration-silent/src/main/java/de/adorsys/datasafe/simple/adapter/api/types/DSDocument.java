package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DSDocument {
    @Delegate
    S100_DSDocument real;

    public DSDocument(DocumentFQN documentFQN, DocumentContent documentContent) {
        real = new S100_DSDocument(
                documentFQN.getReal(),
                documentContent.getReal()
        );
    }

}
