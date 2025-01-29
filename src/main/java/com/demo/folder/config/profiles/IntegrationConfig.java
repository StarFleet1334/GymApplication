package com.demo.folder.config.profiles;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Profile("integration")
public class IntegrationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationConfig.class);

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;


    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Local Configuration is active");
    }

    @Bean
    public DataSource dataSource() {
        LOGGER.info("Setting up DataSource for LOCAL environment");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);

        LOGGER.info("DB URL: {}", dataSource.getUrl());
        LOGGER.info("DB Username: {}", dataSource.getUsername());

        return dataSource;
    }
}