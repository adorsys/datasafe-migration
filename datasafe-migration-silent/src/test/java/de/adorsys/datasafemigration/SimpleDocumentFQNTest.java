package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleDocumentFQNTest {

    @Test
    public void testAddName() {
        DocumentFQN d = blobsFQN("id1");
        Assertions.assertEquals("/blobs/id1", d.getDocusafePath());
    }

    public static DocumentFQN blobsFQN(String blobId) {
        return new DocumentDirectoryFQN("blobs").addName(blobId);
    }
}
