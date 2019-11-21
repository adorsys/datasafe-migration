package de.adorsys.datasafe.simple.adapter.spring;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.spring.factory.LockProviderFactory;
import de.adorsys.datasafe.simple.adapter.spring.factory.SpringSimpleDatasafeServiceFactory;
import de.adorsys.datasafe.simple.adapter.spring.properties.SpringLockProviderProperties;
import de.adorsys.datasafe_1_0_0.simple.adapter.api.types.S100_DFSCredentials;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.SpringPropertiesToDFSCredentialsUtil;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.properties.SpringAmazonS3DFSCredentialsProperties;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.properties.SpringDFSCredentialProperties;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.properties.SpringDatasafeEncryptionProperties;
import de.adorsys.datasafe_1_0_0.simple.adapter.spring.properties.SpringFilesystemDFSCredentialsProperties;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties({
        SpringDFSCredentialProperties.class,
        SpringFilesystemDFSCredentialsProperties.class,
        SpringAmazonS3DFSCredentialsProperties.class,
        SpringDatasafeEncryptionProperties.class,
        SpringLockProviderProperties.class
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
    S100_DFSCredentials dfsCredentials(SpringDFSCredentialProperties properties) {
        return SpringPropertiesToDFSCredentialsUtil.dfsCredentials(properties);
    }

    @Bean
    public SimpleDatasafeService simpleDatasafeService(SpringSimpleDatasafeServiceFactory factory) {
        return factory.getSimpleDataSafeServiceWithSubdir("");

    }

    @Bean
    LockProviderFactory.LockProviderWrapper lockProviderForMigration(SpringLockProviderProperties springLockProviderProperties) {
        log.info("bean for lockprovider retrieved");
        return LockProviderFactory.getFromProperties(springLockProviderProperties);
    }
}
