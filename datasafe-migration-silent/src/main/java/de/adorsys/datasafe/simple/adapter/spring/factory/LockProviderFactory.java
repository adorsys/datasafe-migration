package de.adorsys.datasafe.simple.adapter.spring.factory;

import com.zaxxer.hikari.HikariDataSource;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringLockProviderProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class LockProviderFactory {
    public static LockProviderWrapper getFromProperties(SpringLockProviderProperties springLockProviderProperties) {
        if (springLockProviderProperties.getUrl() == null) {
            log.info("NO URL FOR LOCKPROVIDER GIVEN");
            return new LockProviderWrapper(null);
        }
        log.info("URL FOR LOCKPROVIDER GIVEN");

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(springLockProviderProperties.getUrl());

        if (springLockProviderProperties.getUsername() != null) {
            datasource.setUsername(springLockProviderProperties.getUsername());
        }

        if (springLockProviderProperties.getPassword() != null) {
            datasource.setPassword(springLockProviderProperties.getPassword());
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP(3), locked_at TIMESTAMP(3), locked_by  VARCHAR(255), PRIMARY KEY (name))");
        LockProvider lockProvider = new JdbcTemplateLockProvider(datasource);
        return new LockProviderWrapper(lockProvider);
    }

    @AllArgsConstructor
    @Getter
    public static class LockProviderWrapper {
        private final LockProvider lockProvider;
    }
}
