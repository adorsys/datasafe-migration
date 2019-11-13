package de.adorsys.datasafe.encrypiton.api.types;

import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class UserID {
    @Delegate
    de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserID real;

    public UserID(String name) {
        real = new de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserID(name);
    }
}
