package de.adorsys.datasafe.simple.adapter.spring.datasource;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringMysqlDatasourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class WithMysqlDataSource {
    private static String createTable = "CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP, locked_at TIMESTAMP, locked_by  VARCHAR(255), PRIMARY KEY (name))";

    public static DataSource get(SpringMysqlDatasourceProperties props) {
        MysqlDataSource datasource = new MysqlDataSource();
        datasource.setURL(props.getUrl());

        datasource.setUser(props.getUsername());
        datasource.setPassword(props.getUsername());

        datasource.setPort(props.getPort());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute(createTable);

        return datasource;
    }
}
