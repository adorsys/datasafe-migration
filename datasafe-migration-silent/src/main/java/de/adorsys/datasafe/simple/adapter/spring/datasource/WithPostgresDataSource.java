package de.adorsys.datasafe.simple.adapter.spring.datasource;

import de.adorsys.datasafe.simple.adapter.spring.properties.SpringPostgresDatasourceProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
@SuppressWarnings("Duplicates")
public class WithPostgresDataSource {
    private static String createTable = "CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP, locked_at TIMESTAMP, locked_by  VARCHAR(255), PRIMARY KEY (name))";

    @SneakyThrows
    public static DataSource get(SpringPostgresDatasourceProperties props) {
        Class.forName("org.postgresql.Driver");
        PGSimpleDataSource datasource = new PGSimpleDataSource();
        log.debug("set url to postgres connection");
        log.info("{}",  props.toString());
        datasource.setUrl(props.getUrl());
        datasource.setUser(props.getUsername());
        datasource.setPassword(props.getPassword());

        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute(createTable);

        return datasource;
    }
}
