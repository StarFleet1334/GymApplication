package com.demo.folder.controller.implementation;


import com.demo.folder.controller.skeleton.TrainingControllerInterface;
import com.demo.folder.entity.base.TrainingSession;
import com.demo.folder.entity.dto.request.TrainingRequestDTO;
import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.service.TrainingService;
import com.demo.folder.service.TrainingSessionService;
import com.demo.folder.utils.EntityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class TrainingController implements TrainingControllerInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingController.class);

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Override
    public ResponseEntity<String> create(@Valid @RequestBody TrainingRequestDTO trainingRequestDTO) {
        try {
            trainingService.createTraining(trainingRequestDTO);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body("Training created successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getAll() {
        try {
            return ResponseEntity.ok(trainingService.retrieveAllTrainings());
        } catch (EntityNotFoundException e) {
            return EntityUtil.getObjectResponseNotFoundEntity(e);
        }
    }

    @Override
    public ResponseEntity<Object> createTrainingSession(TrainingSessionDTO trainingSessionDTO) {
        try {
            TrainingSession trainingSession = trainingSessionService.createTrainingSession(trainingSessionDTO);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(trainingSession.getId())
                    .toUri();

            return ResponseEntity.created(location).body("Training session created successfully");
        } catch (EntityNotFoundException e) {
            return EntityUtil.getObjectResponseNotFoundEntity(e);
        }
    }

    @Override
    public ResponseEntity<Void> deleteTrainingSession(Long id) {
        try {
            trainingSessionService.deleteTrainingSession(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @Override
    public ResponseEntity<Object> getAllTrainingSession() {
        List<TrainingSession> trainingSessions = trainingSessionService.getAllTrainingSessions();
        return ResponseEntity.ok(trainingSessions);
    }
}