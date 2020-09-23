package de.adorsys.datasafe.types.api.types;

import de.adorsys.datasafe_1_0_3.types.api.types.S103_ReadKeyPassword;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.function.Supplier;

/**
 * Wrapper for password for reading secret or private key entry.
 */
@Getter
public class ReadKeyPassword {
    @Delegate
    S103_ReadKeyPassword real;

    public ReadKeyPassword(Supplier<char[]> supplierCharArray) {
        real = new S103_ReadKeyPassword(supplierCharArray);
    }
}
