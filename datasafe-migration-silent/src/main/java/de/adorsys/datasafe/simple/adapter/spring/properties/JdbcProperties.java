package de.adorsys.datasafe.simple.adapter.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;

@ConfigurationProperties(prefix = "datasafe.migration.lockprovider.jdbc")
@Validated
@Getter
@Setter
@ToString
public class JdbcProperties {
    @Nullable
    private SpringHikariDatasourceProperties hikari;

    @Nullable
    private SpringMysqlDatasourceProperties mysql;

    @Nullable
    private SpringPostgresDatasourceProperties postgres;
}
