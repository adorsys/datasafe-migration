package de.adorsys.datasafe.simple.adapter.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentDirectoryFQN {
    @Delegate
    de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentDirectoryFQN real;

    public DocumentDirectoryFQN(String s) {
        real = new de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentDirectoryFQN(s);
    }
}
