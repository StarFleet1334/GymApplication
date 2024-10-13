package com.demo.folder.controller.skeleton;

import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Training Type Controller", description = "Operations related to training types")
@RequestMapping(value = "api/training-type", produces = {"application/json"})
public interface TrainingTypeControllerInterface {

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @PostMapping
    @Operation(summary = "Create Training Type", description = "Creates a new training type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created training type"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> createTrainingType(
            @Valid @RequestBody TrainingTypeRequestDTO trainingTypeRequestDTO,
            BindingResult result);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @GetMapping
    @Operation(summary = "Get All Training Types", description = "Retrieves all training types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all training types"),
            @ApiResponse(responseCode = "404", description = "No training types found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TrainingTypeRequestDTO>> getAllTrainingTypes();

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get Training Type By ID", description = "Retrieves a training type by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved training type"),
            @ApiResponse(responseCode = "404", description = "Training type not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainingTypeRequestDTO> getTrainingTypeById(@PathVariable("id") Long id);
}