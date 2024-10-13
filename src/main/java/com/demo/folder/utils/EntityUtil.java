package com.demo.folder.utils;

import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.CreateTraineeRequestDTO;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.entity.dto.response.ErrorResponse;
import com.demo.folder.entity.dto.response.TraineeProfileResponseDTO;
import com.demo.folder.entity.dto.response.TraineeTrainingResponseDTO;
import com.demo.folder.entity.dto.response.TrainerProfileResponseDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityUtil {

    public static ResponseEntity<Object> getObjectResponseEntity(BindingResult result) {
        List<String> errorDetails = result.getAllErrors().stream()
                .map(FieldError.class::cast)
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorResponse errorResponse = new ErrorResponse("Invalid input data", errorDetails);
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    public static ResponseEntity<Object> getObjectResponseNotFoundEntity(EntityNotFoundException e) {
        List<String> errorDetails = Collections.singletonList(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Entity Not Found", errorDetails);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    public static void validateTrainerName(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO.getFirstName() == null || trainerRequestDTO.getFirstName().isEmpty() ||
                trainerRequestDTO.getLastName() == null || trainerRequestDTO.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Trainer's first name or last name must not be empty");
        }
    }

    public static void validateTraineeRequestDTO(CreateTraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO.getFirstName() == null || traineeRequestDTO.getFirstName().isEmpty() ||
                traineeRequestDTO.getLastName() == null || traineeRequestDTO.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Trainee's first name or last name must not be empty");
        }
    }


    public static TrainerProfileResponseDTO createTrainerProfileResponseDTO(Trainer trainer) {
        TrainerProfileResponseDTO responseDTO = new TrainerProfileResponseDTO();
        responseDTO.setFirstName(trainer.getUser().getFirstName());
        responseDTO.setLastName(trainer.getUser().getLastName());
        responseDTO.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        responseDTO.setActive(trainer.getUser().isActive());

        List<TraineeProfileResponseDTO> dtos = trainer.getTrainees().stream()
                .map(trainee -> {
                    TraineeProfileResponseDTO dto = new TraineeProfileResponseDTO();
                    dto.setFirstName(trainee.getUser().getFirstName());
                    dto.setLastName(trainee.getUser().getLastName());
                    dto.setUserName(trainee.getUser().getUsername());
                    return dto;
                }).collect(Collectors.toList());
        responseDTO.setTraineeList(dtos);
        return responseDTO;
    }

    public static TrainerRequestDTO convertToTrainerRequestDTO(Trainer trainer) {
        TrainerRequestDTO trainerRequestDTO = new TrainerRequestDTO();

        if (trainer == null) {
            throw new IllegalArgumentException("Trainer is null");
        }

        if (trainer.getSpecialization() != null) {
            TrainingTypeRequestDTO trainingTypeRequestDTO = new TrainingTypeRequestDTO();
            trainingTypeRequestDTO.setId(trainer.getSpecialization().getId());
            trainingTypeRequestDTO.setTrainingTypeName(trainer.getSpecialization().getTrainingTypeName());
            trainerRequestDTO.setTrainingTypeId(trainingTypeRequestDTO.getId());
        }

        if (trainer.getUser() != null) {
            trainerRequestDTO.setId(trainer.getId());
            trainerRequestDTO.setFirstName(trainer.getUser().getFirstName());
            trainerRequestDTO.setLastName(trainer.getUser().getLastName());
            trainerRequestDTO.setUsername(trainer.getUser().getUsername());
            trainerRequestDTO.setPassword(trainer.getUser().getPassword());
            trainerRequestDTO.setActive(trainer.getUser().isActive());
        }

        if (trainer.getTrainees() != null) {
            List<String> traineesUserNames = trainer.getTrainees().stream()
                    .map(trainee -> trainee.getUser().getUsername())
                    .collect(Collectors.toList());
            trainerRequestDTO.setTrainees(traineesUserNames);
        }

        return trainerRequestDTO;
    }

    public static User createUser(TrainerRequestDTO requestDTO, String plainTextPassword, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setUsername(Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
        user.setPassword(passwordEncoder.encode(plainTextPassword));
        user.setActive(true);
        return user;
    }


    public static TraineeTrainingResponseDTO mapToTraineeTrainingResponseDTO(Training training) {
        TraineeTrainingResponseDTO dto = new TraineeTrainingResponseDTO();
        dto.setTrainerName(training.getTrainer().getUser().getUsername());
        dto.setTrainingName(training.getTrainingName());
        dto.setTrainingType(training.getTrainer().getSpecialization().getTrainingTypeName());
        dto.setTrainingDuration(training.getTrainingDuration());
        dto.setTrainingDate(training.getTrainingDate());
        return dto;
    }



}
