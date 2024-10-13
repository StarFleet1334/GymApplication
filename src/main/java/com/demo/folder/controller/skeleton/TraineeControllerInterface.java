package com.demo.folder.controller.skeleton;

import com.demo.folder.entity.dto.request.*;
import com.demo.folder.entity.dto.response.*;
import com.demo.folder.utils.StatusAction;
import com.demo.folder.utils.TraineeAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainee Controller", description = "Operations related to trainees")
@RequestMapping(value = "api/trainees", produces = {"application/json"})
public interface TraineeControllerInterface {

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @PostMapping
    @Operation(summary = "Register Trainee", description = "Registers a new trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered trainee"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> registerTrainee(
            @Valid @RequestBody CreateTraineeRequestDTO traineeRequestDTO,
            BindingResult result);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @GetMapping
    @Operation(summary = "Get All Trainees", description = "Retrieves a list of all trainees.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainees"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<AllTraineeRequestDTO>> getAllTrainee();

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @PatchMapping("/{username}/{statusAction}")
    @Operation(summary = "Change Trainee Account State", description = "Activates or deactivates a trainee's account based on the action parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed trainee account state"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<String> changeTraineeAccountState(@PathVariable String username,
                                                @PathVariable StatusAction statusAction);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @DeleteMapping("/{username}")
    @Operation(summary = "Delete Trainee", description = "Deletes a trainee from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted trainee"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<String> deleteTrainee(@PathVariable String username);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @GetMapping("/{username}/profile")
    @Operation(summary = "Get Trainee Profile", description = "Retrieves a trainee's profile by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> getTraineeProfile(@PathVariable String username);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @PutMapping("/{username}/trainers/{traineeAction}")
    @Operation(summary = "Update Trainee's Trainers", description = "Updates the list of trainers assigned to a trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee's trainers"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainee or Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<String> updateTraineeTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequestDTO requestDTO,
            @PathVariable TraineeAction traineeAction, BindingResult result);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @PutMapping("/{username}")
    @Operation(summary = "Update Trainee Profile", description = "Updates a trainee's profile information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> updateTrainee(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeProfileRequestDTO requestDTO, BindingResult result);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @GetMapping("/{username}/unassigned-trainers")
    @Operation(summary = "Get Unassigned Trainers", description = "Retrieves trainers not assigned to the trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unassigned trainers"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> getUnassignedTrainers(@PathVariable String username);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINEE')")
    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get Trainee Trainings", description = "Retrieves trainings assigned to the trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee trainings"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Object> getTrainings(
            @PathVariable String username,
            @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
            @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
            @RequestParam(name = "trainingName", required = false) String trainingName,
            @RequestParam(name = "trainingType", required = false) String trainingType
    );
}