package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_1.simple.adapter.api.types.S101_DocumentContent;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentContent {
    @Delegate
    S101_DocumentContent real;

    public DocumentContent(byte[] value) {
        real = new S101_DocumentContent(value);
    }

}
