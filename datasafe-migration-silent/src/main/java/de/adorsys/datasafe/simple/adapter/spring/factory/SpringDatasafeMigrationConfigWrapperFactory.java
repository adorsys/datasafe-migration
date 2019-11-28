package de.adorsys.datasafe.simple.adapter.spring.factory;

import de.adorsys.datasafe.simple.adapter.impl.DatasafeMigrationConfig;
import de.adorsys.datasafe.simple.adapter.spring.datasource.WithHikariDataSource;
import de.adorsys.datasafe.simple.adapter.spring.datasource.WithMysqlDataSource;
import de.adorsys.datasafe.simple.adapter.spring.properties.JdbcProperties;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringDatasafeMigrationProperties;
import de.adorsys.datasafemigration.MigrationException;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;

import javax.sql.DataSource;

@Slf4j
public class SpringDatasafeMigrationConfigWrapperFactory {
    public static DatasafeMigrationConfig getFromProperties(SpringDatasafeMigrationProperties properties) {
        if (properties.getLockprovider() == null) {
            log.info("NO URL FOR LOCKPROVIDER GIVEN");
            return new DatasafeMigrationConfig(null, Boolean.FALSE);
        }

        JdbcProperties jdbc = properties.getLockprovider().getJdbc();

        DataSource dataSource = null;
        if (jdbc.getHikari() != null) {
            dataSource = WithHikariDataSource.get(jdbc.getHikari());
        }
        if (jdbc.getMysql() != null) {
            dataSource = WithMysqlDataSource.get(jdbc.getMysql());
        }
        if (dataSource == null) {
            throw new MigrationException("Specification of jdbc not given in properties");
        }
        log.info("URL FOR LOCKPROVIDER GIVEN");

        Boolean migrationDoNewFolder = Boolean.FALSE;
        if (properties.getDistinctfolder() != null) {
            migrationDoNewFolder = properties.getDistinctfolder();
        }

        LockProvider lockProvider = new JdbcTemplateLockProvider(dataSource);
        return new DatasafeMigrationConfig(lockProvider, migrationDoNewFolder);
    }

}
