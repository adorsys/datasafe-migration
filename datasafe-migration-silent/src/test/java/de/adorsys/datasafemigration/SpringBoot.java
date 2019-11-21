package de.adorsys.datasafemigration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;

@Slf4j
@SpringBootConfiguration
public class SpringBoot {
    private static JDBCServerFactory factory = null;
    public SpringBoot() {
        log.info("SPRING BOOT INIT DONE HERE");
        if (factory == null) {
            factory = new JDBCServerFactory();
            factory.start();
        }
    }

}
