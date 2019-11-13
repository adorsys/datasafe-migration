package de.adorsys.datasafe.types.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.function.Supplier;

/**
 * Wrapper for password for reading secret or private key entry.
 */
@Getter
public class ReadKeyPassword {
    @Delegate
    de.adorsys.datasafe_1_0_0.types.api.types.ReadKeyPassword real;

    public ReadKeyPassword(Supplier<char[]> supplierCharArray) {
        real = new de.adorsys.datasafe_1_0_0.types.api.types.ReadKeyPassword(supplierCharArray);
    }
}
