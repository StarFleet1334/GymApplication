package com.demo.folder.controller;

import com.demo.folder.entity.base.TrainingSession;
import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import com.demo.folder.service.TrainingSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CircuitBreakerTest {

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Test
    public void testCreateTrainingSessionCircuitBreaker() {
        TrainingSessionDTO invalidDto = new TrainingSessionDTO();
        invalidDto.setTrainerUserName("nonexistentUser");
        invalidDto.setTrainerFirstName("First");
        invalidDto.setTrainerLastName("Last");
        invalidDto.setTrainingDate(LocalDate.now());
        invalidDto.setTrainingDuration(1.0);
        for (int i = 0; i < 15; i++) {
            try {
                trainingSessionService.createTrainingSession(invalidDto);
            } catch (Exception e) {
            }
        }
        TrainingSession response = trainingSessionService.createTrainingSession(invalidDto);
        assertNotNull(response);
    }

    @Test
    public void testDeleteTrainingSessionCircuitBreaker() {
        trainingSessionService.resetFallbackFlag();
        Long nonExistentId = -1L;
        for (int i = 0; i < 15; i++) {
            try {
                trainingSessionService.deleteTrainingSession(nonExistentId);
            } catch (Exception ignored) {
            }
        }
        trainingSessionService.deleteTrainingSession(nonExistentId);
        assertTrue(trainingSessionService.isFallbackCalled(), "Fallback method was not called");
        trainingSessionService.resetFallbackFlag();
    }
}
