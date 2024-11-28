package com.demo.folder.entity.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TrainingSessionDTO {
    @NotBlank(message = "Trainer userName is required")
    private String trainerUserName;
    @NotBlank(message = "Trainer first name is required")
    private String trainerFirstName;
    @NotBlank(message = "Trainer last name is required")
    private String trainerLastName;
    @NotNull(message = "isActive status is required")
    private Boolean isActive;
    @NotNull(message = "Training date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration is required")
    private Number trainingDuration;


    public @NotBlank(message = "Trainer userName is required") String getTrainerUserName() {
        return trainerUserName;
    }

    public void setTrainerUserName(
            @NotBlank(message = "Trainer userName is required") String trainerUserName) {
        this.trainerUserName = trainerUserName;
    }

    public @NotBlank(message = "Trainer first name is required") String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(
            @NotBlank(message = "Trainer first name is required") String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public @NotBlank(message = "Trainer last name is required") String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(
            @NotBlank(message = "Trainer last name is required") String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public @NotNull(message = "isActive status is required") Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(
            @NotNull(message = "isActive status is required") Boolean isActive) {
        this.isActive = isActive;
    }

    public @NotNull(message = "Training date is required") LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(
            @NotNull(message = "Training date is required") LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public @NotNull(message = "Training duration is required") Number getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(
            @NotNull(message = "Training duration is required") Number trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
