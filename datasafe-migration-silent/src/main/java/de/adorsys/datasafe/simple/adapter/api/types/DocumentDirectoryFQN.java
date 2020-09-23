package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentDirectoryFQN;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@EqualsAndHashCode
public class DocumentDirectoryFQN {
    @Delegate (excludes = Without.class)
    S103_DocumentDirectoryFQN real;

    public DocumentDirectoryFQN(String s) {
        real = new S103_DocumentDirectoryFQN(s);
    }

    public DocumentDirectoryFQN addDirectory(String s) {
        return new DocumentDirectoryFQN(real.addDirectory(s).getDocusafePath());
    }
    public DocumentFQN addName(String s) {
        return new DocumentFQN(real.addName(s).getDocusafePath());

    }

    private static interface Without {
        DocumentDirectoryFQN addDirectory(String s);
        DocumentFQN addName(String s);
    }
}
