package de.adorsys.datasafe.simple.adapter.spring.datasource;

import com.zaxxer.hikari.HikariDataSource;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringHikariDatasourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class WithHikariDataSource {
    private static String createTable = "CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP(3), locked_at TIMESTAMP(3), locked_by  VARCHAR(255), PRIMARY KEY (name))";

    public static DataSource get(SpringHikariDatasourceProperties props) {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(props.getUrl());

        datasource.setUsername(props.getUsername());

        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute(createTable);

        return datasource;
    }
}
