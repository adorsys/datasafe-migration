package de.adorsys.datasafe.simple.adapter.spring.datasource;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringMysqlDatasourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
public class WithMysqlDataSource {
    private static String createTable = "CREATE TABLE IF NOT EXISTS shedlock(name VARCHAR(64), lock_until TIMESTAMP, locked_at TIMESTAMP, locked_by  VARCHAR(255), PRIMARY KEY (name))";

    public static DataSource get(SpringMysqlDatasourceProperties props) {

        MysqlDataSource datasource = new MysqlDataSource();
        log.debug("set url to mysql connection");
        datasource.setUrl(props.getUrl());
        datasource.setUser(props.getUsername());
        datasource.setPassword(props.getPassword());


        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.execute(createTable);


        return datasource;
    }
}
