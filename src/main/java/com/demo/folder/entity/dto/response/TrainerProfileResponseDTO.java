package com.demo.folder.entity.dto.response;

import java.util.List;

public class TrainerProfileResponseDTO {

    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeProfileResponseDTO> traineeList;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<TraineeProfileResponseDTO> getTraineeList() {
        return traineeList;
    }

    public void setTraineeList(
            List<TraineeProfileResponseDTO> traineeList) {
        this.traineeList = traineeList;
    }
}