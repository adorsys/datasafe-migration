package de.adorsys.datasafe.encrypiton.api.types;

import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserID;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class UserID {
    @Delegate
    S100_UserID real;

    public UserID(String name) {
        real = new S100_UserID(name);
    }
}
