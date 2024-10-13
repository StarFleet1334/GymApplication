package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

public class TraineeTrainingsListRequestDTO {

    @NotBlank(message = "UserName is required")
    private String userName;

    private Date periodFrom;
    private Date periodTo;
    private String trainingName;
    private String trainingType;

    public @NotBlank(message = "UserName is required") String getUserName() {
        return userName;
    }

    public void setUserName(
            @NotBlank(message = "UserName is required") String userName) {
        this.userName = userName;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }
}