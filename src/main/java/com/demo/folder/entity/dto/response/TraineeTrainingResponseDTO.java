package com.demo.folder.entity.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeTrainingResponseDTO {

    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Number trainingDuration;
    private String trainerName;

}