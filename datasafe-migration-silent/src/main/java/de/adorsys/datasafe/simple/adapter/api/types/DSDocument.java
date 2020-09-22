package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DSDocument;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DSDocument {
    private DocumentFQN documentFQN;
    private DocumentContent documentContent;

    public S103_DSDocument getReal() {
        return new S103_DSDocument(documentFQN.getReal(), documentContent.getReal());
    }
}
