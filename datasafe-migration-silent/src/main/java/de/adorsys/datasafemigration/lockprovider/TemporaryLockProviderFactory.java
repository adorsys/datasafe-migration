package de.adorsys.datasafemigration.lockprovider;

import com.zaxxer.hikari.HikariDataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.hsqldb.Server;
import org.springframework.jdbc.core.JdbcTemplate;

public class TemporaryLockProviderFactory {
    public static LockProvider get() {
        Server server = new Server();
        server.setDatabaseName(0, "test");
        server.setDatabasePath(0, "./test");
        server.start();

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl("jdbc:hsqldb:hsql://localhost/test");
        datasource.setUsername("SA");
        // datasource.setPassword("SA");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP(3), locked_at TIMESTAMP(3), locked_by  VARCHAR(255), PRIMARY KEY (name))");
        LockProvider lockProvider = new JdbcTemplateLockProvider(datasource);
        return lockProvider;
    }
}
