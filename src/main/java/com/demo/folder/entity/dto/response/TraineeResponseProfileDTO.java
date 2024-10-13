package com.demo.folder.entity.dto.response;

import java.util.Date;
import java.util.List;

public class TraineeResponseProfileDTO {

    private String firstName;
    private String lastName;
    private Date date_of_birth;
    private String address;
    private boolean isActive;
    private List<TrainerResponseProfileDTO> trainerResponseProfileDTOList;
    private List<TraineeTrainingResponseDTO> trainings;


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

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<TrainerResponseProfileDTO> getTrainerProfileList() {
        return trainerResponseProfileDTOList;
    }

    public void setTrainerProfileList(
            List<TrainerResponseProfileDTO> trainerResponseProfileDTOList) {
        this.trainerResponseProfileDTOList = trainerResponseProfileDTOList;
    }

    public List<TraineeTrainingResponseDTO> getTrainings() {
        return trainings;
    }

    public void setTrainings(
            List<TraineeTrainingResponseDTO> trainings) {
        this.trainings = trainings;
    }
}