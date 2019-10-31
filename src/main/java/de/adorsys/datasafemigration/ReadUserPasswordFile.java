package de.adorsys.datasafemigration;

import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserID;
import de.adorsys.datasafe_0_7_1.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_0_7_1.types.api.types.ReadKeyPassword;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReadUserPasswordFile {

    @SneakyThrows
    public static List<UserIDAuth> getAllUsers(String filename) {
        List<UserIDAuth> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (! (line.startsWith("#") || line.replaceAll(" ","").length() == 0)) {
                // Line does not start with a # and is not empty
                int delimiter = line.indexOf(" ");
                String username = line.substring(0, delimiter);
                String password = line.substring(delimiter+1);
                Supplier<char[]> passwordSupplier = password::toCharArray;
                list.add(new UserIDAuth(new UserID(username), new ReadKeyPassword(passwordSupplier)));
            }
        }
        return list;
    }
}
