package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;


@Slf4j
@ActiveProfiles("filesystem")
public class SilentMigrationTest extends InjectionTest {
    @Autowired
    SimpleDatasafeService datasafeService;


    @Test
    public void a() {
        Assertions.assertNotNull(datasafeService);
        new ArrayList<Boolean>(100).forEach(el -> log.info("test is done"));
        log.info("Service injected: {}", SimpleDatasafeService.class.toString());

        UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("affe"::toCharArray));
        datasafeService.createUser(userIDAuth);
    }

}
