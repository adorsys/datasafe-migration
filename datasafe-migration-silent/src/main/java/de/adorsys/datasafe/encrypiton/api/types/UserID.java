package de.adorsys.datasafe.encrypiton.api.types;

import de.adorsys.datasafe_1_0_1.encrypiton.api.types.S101_UserID;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class UserID {
    @Delegate
    S101_UserID real;

    public UserID(String name) {
        real = new S101_UserID(name);
    }
}
