package com.demo.folder.controller.skeleton;

import com.demo.folder.entity.dto.request.TrainingRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Training Controller", description = "Operations related to trainings")
@RequestMapping(value = "api/trainings", produces = {"application/json"})
public interface TrainingControllerInterface {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create Training", description = "Creates a new training session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Related entity not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<String> create(@Valid @RequestBody TrainingRequestDTO trainingRequestDTO);

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get All Trainings", description = "Retrieves all training sessions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainings"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> getAll();
}