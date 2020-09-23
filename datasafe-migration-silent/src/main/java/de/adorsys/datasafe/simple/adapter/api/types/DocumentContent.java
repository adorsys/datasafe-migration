package de.adorsys.datasafe.simple.adapter.api.types;

import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DocumentContent;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentContent {
    @Delegate
    S103_DocumentContent real;

    public DocumentContent(byte[] value) {
        real = new S103_DocumentContent(value);
    }

}
