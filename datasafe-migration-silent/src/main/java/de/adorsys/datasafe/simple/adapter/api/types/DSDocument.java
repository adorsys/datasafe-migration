package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DSDocument;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DSDocument {
    private DocumentFQN documentFQN;
    private DocumentContent documentContent;

    public S100_DSDocument getReal() {
        return new S100_DSDocument(documentFQN.getReal(), documentContent.getReal());
    }
}
