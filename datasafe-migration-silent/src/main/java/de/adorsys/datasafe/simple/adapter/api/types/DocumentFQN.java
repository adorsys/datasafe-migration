package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentFQN;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@EqualsAndHashCode
public class DocumentFQN {
    @Delegate
    S101_DocumentFQN real;

    public DocumentFQN(String docusafePath) {
        real = new S101_DocumentFQN(docusafePath);
    }
}
