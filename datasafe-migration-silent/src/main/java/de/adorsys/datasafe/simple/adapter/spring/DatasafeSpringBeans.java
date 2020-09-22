package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.impl.DatasafeMigrationConfig;
import de.adorsys.datasafe.simple.adapter.spring.factory.SpringDatasafeMigrationConfigWrapperFactory;
import de.adorsys.datasafe.simple.adapter.spring.factory.SpringSimpleDatasafeServiceFactory;
import de.adorsys.datasafe.simple.adapter.spring.properties.JdbcProperties;
import de.adorsys.datasafe.simple.adapter.spring.properties.LockProviderProperties;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringDatasafeMigrationProperties;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringHikariDatasourceProperties;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringMysqlDatasourceProperties;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringPostgresDatasourceProperties;
import de.adorsys.datasafe_1_0_3.simple.adapter.api.types.S103_DFSCredentials;
import de.adorsys.datasafe_1_0_3.simple.adapter.spring.SpringPropertiesToDFSCredentialsUtil;
import de.adorsys.datasafe_1_0_3.simple.adapter.spring.properties.SpringAmazonS3DFSCredentialsProperties;
import de.adorsys.datasafe_1_0_3.simple.adapter.spring.properties.SpringDFSCredentialProperties;
import de.adorsys.datasafe_1_0_3.simple.adapter.spring.properties.SpringDatasafeEncryptionProperties;
import de.adorsys.datasafe_1_0_3.simple.adapter.spring.properties.SpringFilesystemDFSCredentialsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SpringDFSCredentialProperties.class,
        SpringFilesystemDFSCredentialsProperties.class,
        SpringAmazonS3DFSCredentialsProperties.class,
        SpringDatasafeEncryptionProperties.class,
        SpringHikariDatasourceProperties.class,
        SpringMysqlDatasourceProperties.class,
        SpringPostgresDatasourceProperties.class,
        JdbcProperties.class,
        LockProviderProperties.class,
        SpringDatasafeMigrationProperties.class
})
@Slf4j
public class DatasafeSpringBeans {

    public DatasafeSpringBeans() {
        log.info("INIT of DatasafeSpringBeans");
    }

    @Bean
    SpringSimpleDatasafeServiceFactory simpleDatasafeServiceFactory() {
        return new SpringSimpleDatasafeServiceFactory();
    }

    @Bean
    S103_DFSCredentials dfsCredentials(SpringDFSCredentialProperties properties) {
        return SpringPropertiesToDFSCredentialsUtil.dfsCredentials(properties);
    }

    @Bean
    public SimpleDatasafeService simpleDatasafeService(SpringSimpleDatasafeServiceFactory factory) {
        return factory.getSimpleDataSafeServiceWithSubdir("");

    }

    @Bean
    DatasafeMigrationConfig lockProviderForMigration(SpringDatasafeMigrationProperties springDatasafeMigrationProperties) {
        log.info("bean for lockprovider retrieved");
        return SpringDatasafeMigrationConfigWrapperFactory.getFromProperties(springDatasafeMigrationProperties);
    }
}
