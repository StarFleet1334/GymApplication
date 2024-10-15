package com.demo.folder.controller.implementation;


import com.demo.folder.controller.skeleton.TrainingControllerInterface;
import com.demo.folder.entity.dto.request.TrainingRequestDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.service.TrainingService;
import com.demo.folder.utils.EntityUtil;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TrainingController implements TrainingControllerInterface {

    @Autowired
    private TrainingService trainingService;

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
}