package de.adorsys.datasafe.simple.adapter.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;

@ConfigurationProperties(prefix = "datasafe.migration.lockprovider.jdbc.hikari")
@Validated
@Getter
@Setter
@ToString
public class SpringHikariDatasourceProperties {
    private String url;
    private String username;
}
