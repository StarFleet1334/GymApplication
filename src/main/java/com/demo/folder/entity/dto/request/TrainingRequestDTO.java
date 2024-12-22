package com.demo.folder.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TrainingRequestDTO {

    @NotBlank(message = "Trainee userName is required")
    private String traineeUserName;
    @NotBlank(message = "Trainer userName is required")
    private String trainerUserName;
    @NotBlank(message = "Training name is required")
    private String trainingName;
    @NotNull(message = "Training date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration is required")
    private Number duration;

    public @NotBlank(message = "Trainee userName is required") String getTraineeUserName() {
        return traineeUserName;
    }

    public @NotBlank(message = "Trainer userName is required") String getTrainerUserName() {
        return trainerUserName;
    }

    public @NotNull(message = "Training date is required") LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(
            @NotNull(message = "Training date is required") LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public @NotNull(message = "Training duration is required") Number getDuration() {
        return duration;
    }


}