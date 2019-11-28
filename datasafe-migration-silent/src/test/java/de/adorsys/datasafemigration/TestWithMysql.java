package de.adorsys.datasafemigration;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.spring.annotations.UseDatasafeSpringConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Slf4j
@ActiveProfiles("mysql")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@UseDatasafeSpringConfiguration
@DirtiesContext(classMode=AFTER_EACH_TEST_METHOD)
public class TestWithMysql {
    @Autowired
    SimpleDatasafeService datasafeService;

    // @Test
    public void checkAutowire() {
        Assertions.assertNotNull(datasafeService);
    }

}
