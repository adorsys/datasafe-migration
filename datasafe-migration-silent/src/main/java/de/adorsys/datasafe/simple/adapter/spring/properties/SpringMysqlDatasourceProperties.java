package de.adorsys.datasafe.simple.adapter.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "datasafe.migration.lockprovider.jdbc.mysql")
@Validated
@Getter
@Setter
@ToString
public class SpringMysqlDatasourceProperties {
    private String url;
    private String username;
    private String password;

}
