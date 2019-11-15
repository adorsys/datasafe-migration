package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentDirectoryFQN;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentDirectoryFQN {
    @Delegate
    S100_DocumentDirectoryFQN real;

    public DocumentDirectoryFQN(String s) {
        real = new S100_DocumentDirectoryFQN(s);
    }
}
