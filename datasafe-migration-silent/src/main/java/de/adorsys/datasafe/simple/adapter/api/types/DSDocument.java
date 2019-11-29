package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DSDocument;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DSDocument {
    private DocumentFQN documentFQN;
    private DocumentContent documentContent;

    public S101_DSDocument getReal() {
        return new S101_DSDocument(documentFQN.getReal(), documentContent.getReal());
    }
}
