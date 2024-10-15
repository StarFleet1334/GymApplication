package com.demo.folder.controller.implementation;

import com.demo.folder.controller.skeleton.TrainingTypeControllerInterface;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.service.TrainingTypeService;
import com.demo.folder.utils.EntityUtil;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TrainingTypeController implements TrainingTypeControllerInterface {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Override
    public ResponseEntity<Object> createTrainingType(
            @Valid @RequestBody TrainingTypeRequestDTO trainingTypeRequestDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return EntityUtil.getObjectResponseEntity(result);
            }
            trainingTypeService.createTrainingType(trainingTypeRequestDTO);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .build()
                    .toUri();
            return ResponseEntity.created(location).body("Training type created successfully.");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<TrainingTypeRequestDTO>> getAllTrainingTypes() {
        List<TrainingTypeRequestDTO> trainingTypes = trainingTypeService.retrieveAllTrainingTypes();

        if (trainingTypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(trainingTypes);
    }

    @Override
    public ResponseEntity<TrainingTypeRequestDTO> getTrainingTypeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(trainingTypeService.getTrainingTypeById(id));
    }

}