package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
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
    }

}
