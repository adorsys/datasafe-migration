package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentFQN;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@EqualsAndHashCode
public class DocumentFQN {
    @Delegate
    S103_DocumentFQN real;

    public DocumentFQN(String docusafePath) {
        real = new S103_DocumentFQN(docusafePath);
    }
}
