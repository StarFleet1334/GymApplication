package com.demo.folder.controller.skeleton;

import com.demo.folder.entity.dto.request.*;
import com.demo.folder.entity.dto.response.*;
import com.demo.folder.utils.StatusAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainer Controller", description = "Operations related to trainers")
@RequestMapping(value = "api/trainers", produces = {"application/json"})
public interface TrainerControllerInterface {

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @PostMapping
    @Operation(summary = "Register Trainer", description = "Registers a new trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered trainer"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> registerTrainer(@Valid @RequestBody TrainerRequestDTO trainerRequestDTO,
                                      BindingResult result);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @GetMapping
    @Operation(summary = "Get All Trainers", description = "Retrieves all trainers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TrainerRequestDTO>> getAllTrainee();

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @PatchMapping("/{username}/{statusAction}")
    @Operation(summary = "Change Trainer Account State", description = "Activates or deactivates a trainer's account based on the action parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed trainer account state"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<String> changeTrainerAccountState(@PathVariable String username,
                                                @PathVariable StatusAction statusAction);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @GetMapping("/{username}")
    @Operation(summary = "Get Trainer Profile", description = "Retrieves a trainer's profile by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainerProfileResponseDTO> getTrainerProfile(@PathVariable String username);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @PutMapping("/{username}")
    @Operation(summary = "Update Trainer Profile", description = "Updates a trainer's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainerProfileResponseDTO> updateTrainerProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerProfileRequestDTO requestDTO);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get Trainer's Trainings", description = "Retrieves trainings associated with a trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer's trainings"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> getTrainings(
            @PathVariable String username,
            @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(name = "traineeName", required = false) String traineeName);
}