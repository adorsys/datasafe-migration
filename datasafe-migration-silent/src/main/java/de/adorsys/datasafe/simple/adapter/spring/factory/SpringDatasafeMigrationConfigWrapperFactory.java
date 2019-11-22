package de.adorsys.datasafe.simple.adapter.spring.factory;

import com.zaxxer.hikari.HikariDataSource;
import de.adorsys.datasafe.simple.adapter.impl.DatasafeMigrationConfig;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringDatasafeMigrationProperties;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class SpringDatasafeMigrationConfigWrapperFactory {
    public static DatasafeMigrationConfig getFromProperties(SpringDatasafeMigrationProperties springDatasafeMigrationProperties) {
        if (springDatasafeMigrationProperties.getUrl() == null) {
            log.info("NO URL FOR LOCKPROVIDER GIVEN");
            return new DatasafeMigrationConfig(null, Boolean.FALSE);
        }
        log.info("URL FOR LOCKPROVIDER GIVEN");

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(springDatasafeMigrationProperties.getUrl());

        if (springDatasafeMigrationProperties.getUsername() != null) {
            datasource.setUsername(springDatasafeMigrationProperties.getUsername());
        }

        if (springDatasafeMigrationProperties.getPassword() != null) {
            datasource.setPassword(springDatasafeMigrationProperties.getPassword());
        }

        Boolean migrationDoNewFolder = Boolean.FALSE;
        if (springDatasafeMigrationProperties.getDistinctfolder()!= null) {
            migrationDoNewFolder = springDatasafeMigrationProperties.getDistinctfolder();
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP(3), locked_at TIMESTAMP(3), locked_by  VARCHAR(255), PRIMARY KEY (name))");
        LockProvider lockProvider = new JdbcTemplateLockProvider(datasource);
        return new DatasafeMigrationConfig(lockProvider, migrationDoNewFolder);
    }

}
