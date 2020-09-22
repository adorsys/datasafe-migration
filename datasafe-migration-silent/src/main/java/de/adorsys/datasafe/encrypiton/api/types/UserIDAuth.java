package de.adorsys.datasafe.encrypiton.api.types;


import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserIDAuth;
import lombok.Getter;

/**
 * Wrapper that represents username and password.
 */
@Getter
public class UserIDAuth {
    S103_UserIDAuth real;

    public UserID getUserID() {
        return new UserID(real.getUserID().getValue());
    }

    public ReadKeyPassword getReadKeyPassword() {
        return new ReadKeyPassword(new String(real.getReadKeyPassword().getValue())::toCharArray);
    }

    public UserIDAuth(UserID userid, ReadKeyPassword readKeyPassword) {
        real = new S103_UserIDAuth(userid.getReal(), readKeyPassword.getReal());
    }

}
