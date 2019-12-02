package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentDirectoryFQN;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@EqualsAndHashCode
public class DocumentDirectoryFQN {
    @Delegate (excludes = Without.class)
    S101_DocumentDirectoryFQN real;

    public DocumentDirectoryFQN(String s) {
        real = new S101_DocumentDirectoryFQN(s);
    }

    public DocumentDirectoryFQN addDirectory(String s) {
        return new DocumentDirectoryFQN(real.addDirectory(s).getDocusafePath());
    }

    private static interface Without {
        public DocumentDirectoryFQN addDirectory(String s);
    }
}
