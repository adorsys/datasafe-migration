package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.spring.annotations.UseDatasafeSpringConfiguration;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserID;
import de.adorsys.datasafe_1_0_0.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe_1_0_0.types.api.types.ReadKeyPassword;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootConfiguration
@UseDatasafeSpringConfiguration
public class InjectionTest extends WithStorageProvider {

    public void testCreateUser(SimpleDatasafeService datasafeService) {
        Assertions.assertThat(datasafeService).isNotNull();
        UserID userid = new UserID("peter");
        ReadKeyPassword password = new ReadKeyPassword("password"::toCharArray);
        UserIDAuth userIDAuth = new UserIDAuth(userid, password);
        Assertions.assertThat(datasafeService.userExists(userid)).isFalse();
        datasafeService.createUser(userIDAuth);
        Assertions.assertThat(datasafeService.userExists(userid)).isTrue();
        datasafeService.destroyUser(userIDAuth);
    }
}
