package com.demo.folder.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class EnvProfileServiceCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvProfileServiceCheck.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    public void checkActiveProfiles() {
        String[] profiles = environment.getActiveProfiles();
        LOGGER.info("Currently active profile - {}", Arrays.toString(profiles));
    }
}