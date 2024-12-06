package com.demo.folder.health.prome;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class TrainerExecutionTime {

    private final Timer methodExecutionTimer;
    private final MeterRegistry meterRegistry;

    public TrainerExecutionTime(MeterRegistry registry) {
        this.meterRegistry = registry;

        methodExecutionTimer = Timer.builder("trainer.registration.time")
                .description("Time taken to execute method: <Create Trainer>")
                .publishPercentileHistogram()
                .register(registry);

    }

    public <T> T recordExecutionTime(Callable<T> callable) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return callable.call();
        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Method execution was interrupted.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Illegal arguments provided to method.", e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during method execution.", e);
        } finally {
            sample.stop(methodExecutionTimer);
        }
    }
}