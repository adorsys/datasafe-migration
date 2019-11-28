package de.adorsys.datasafe.simple.adapter.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "datasafe.migration.lockprovider")
@Validated
@Getter
@Setter
@ToString
public class LockProviderProperties {
    private JdbcProperties jdbc;
}
