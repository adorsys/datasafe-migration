package de.adorsys.datasafe.simple.adapter.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class DocumentContent {
    @Delegate
    de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentContent real;

    public DocumentContent(byte[] value) {
        real = new de.adorsys.datasafe_1_0_0.simple.adapter.api.types.DocumentContent(value);
    }

}
