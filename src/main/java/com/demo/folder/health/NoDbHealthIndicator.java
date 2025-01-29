package com.demo.folder.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration")
public class NoDbHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().withDetail("Database Status", "Integration Disabled").build();
    }
}
