package de.adorsys.datasafemigration.offline;

import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserID;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.S100_UserIDAuth;
import de.adorsys.datasafe_1_0_0.types.api.types.S100_ReadKeyPassword;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class ReadUserPasswordFile {
    @SneakyThrows
    public static List<S100_UserIDAuth> getAllUsers(String filename) {
        List<S100_UserIDAuth> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (! (line.startsWith("#") || line.replaceAll(" ","").length() == 0)) {
                // Line does not start with a # and is not empty
                int delimiter = line.indexOf(" ");
                String username = line.substring(0, delimiter);
                String password = line.substring(delimiter+1);
                Supplier<char[]> passwordSupplier = password::toCharArray;
                list.add(new S100_UserIDAuth(new S100_UserID(username), new S100_ReadKeyPassword(passwordSupplier)));
            }
        }
        log.debug("read {} user from {}", list.size(), filename);
        return list;
    }
}
