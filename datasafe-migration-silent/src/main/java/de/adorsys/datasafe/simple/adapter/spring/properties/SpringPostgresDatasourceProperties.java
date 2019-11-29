package de.adorsys.datasafe.simple.adapter.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "datasafe.migration.lockprovider.jdbc.postgres")
@Validated
@Getter
@Setter
@ToString
public class SpringPostgresDatasourceProperties {
    private String url;
    private String username;
    private String password;
}
