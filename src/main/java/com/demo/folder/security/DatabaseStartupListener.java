package com.demo.folder.security;

import com.demo.folder.config.DatabaseDataInitializer;
import com.demo.folder.service.DatabaseInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class DatabaseStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseStartupListener.class);

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    private DatabaseDataInitializer databaseDataInitializer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Database startup process initiated...");
        LOGGER.info("Starting DatabaseInitializer...");

        databaseInitializer.init();

        LOGGER.info("Starting DatabaseDataInitializer...");
        databaseDataInitializer.initData();

        LOGGER.info("Database startup process completed.");
    }
}
