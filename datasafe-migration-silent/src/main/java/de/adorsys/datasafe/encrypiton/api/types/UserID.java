package de.adorsys.datasafe.encrypiton.api.types;

import de.adorsys.datasafe_1_0_3.encrypiton.api.types.S103_UserID;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class UserID {
    @Delegate
    S103_UserID real;

    public UserID(String name) {
        real = new S103_UserID(name);
    }
}
