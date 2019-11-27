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
public class SpringDatasafeMigrationProperties {

    @Nullable
    private String url;

    @Nullable
    private String username;

    @Nullable
    private String password;

    @Nullable
    private String createtablecommand;

    @Nullable
    private Boolean distinctfolder;
}
