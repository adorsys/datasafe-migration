package de.adorsys.datasafe.simple.adapter.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentFQN {
    @Delegate
    de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentFQN real;

    public DocumentFQN(String docusafePath) {
        real = new de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentFQN(docusafePath);
    }
}
