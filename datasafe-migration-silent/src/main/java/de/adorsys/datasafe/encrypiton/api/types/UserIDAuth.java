package de.adorsys.datasafe.encrypiton.api.types;


import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import lombok.experimental.Delegate;

/**
 * Wrapper that represents username and password.
 */
public class UserIDAuth {
    @Delegate
    de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth real;

    public UserIDAuth(UserID peter, ReadKeyPassword readKeyPassword) {
        real = new de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth(peter.getReal(), readKeyPassword.getReal());
    }
}
