package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentFQN;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@EqualsAndHashCode
public class DocumentFQN {
    @Delegate
    S100_DocumentFQN real;

    public DocumentFQN(String docusafePath) {
        real = new S100_DocumentFQN(docusafePath);
    }
}
