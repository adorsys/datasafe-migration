package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DocumentContent;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentContent {
    @Delegate
    S100_DocumentContent real;

    public DocumentContent(byte[] value) {
        real = new S100_DocumentContent(value);
    }

}
