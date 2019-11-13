package de.adorsys.datasafe.encrypiton.api.types;


import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import lombok.Getter;

/**
 * Wrapper that represents username and password.
 */
@Getter
public class UserIDAuth {
    de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth real;

    public UserID getUserID() {
        return new UserID(real.getUserID().getValue());
    }

    public ReadKeyPassword getReadKeyPassword() {
        return new ReadKeyPassword(new String(real.getReadKeyPassword().getValue())::toCharArray);
    }

    public UserIDAuth(UserID peter, ReadKeyPassword readKeyPassword) {
        real = new de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth(peter.getReal(), readKeyPassword.getReal());
    }

}
