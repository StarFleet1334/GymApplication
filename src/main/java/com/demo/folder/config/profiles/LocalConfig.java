package com.demo.folder.config.profiles;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Profile("local")
public class LocalConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfig.class);

    private static final String DRIVER_CLASS_NAME = "org.h2.Driver";
    private static final String DATABASE_URL = "jdbc:h2:mem:localdb;DB_CLOSE_DELAY=-1";
    private static final String DATABASE_USERNAME = "sa";
    private static final String DATABASE_PASSWORD = "";

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Local Configuration is active");
    }

    @Bean
    public DataSource dataSource() {
        LOGGER.info("Setting up DataSource for LOCAL environment");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(DATABASE_URL);
        dataSource.setUsername(DATABASE_USERNAME);
        dataSource.setPassword(DATABASE_PASSWORD);

        LOGGER.info("DB URL: {}", dataSource.getUrl());
        LOGGER.info("DB Username: {}", dataSource.getUsername());

        return dataSource;
    }
}