package com.demo.folder.entity.dto.response;

import java.time.LocalDate;

public class TrainerTrainingResponseDTO {

    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Number trainingDuration;
    private String traineeName;

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public Number getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Number trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public String getTraineeName() {
        return traineeName;
    }

    public void setTraineeName(String traineeName) {
        this.traineeName = traineeName;
    }
}