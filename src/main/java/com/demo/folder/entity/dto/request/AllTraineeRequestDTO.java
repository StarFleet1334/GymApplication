package com.demo.folder.entity.dto.request;

import com.demo.folder.entity.dto.response.TraineeTrainingResponseDTO;
import java.time.LocalDate;
import java.util.List;

public class AllTraineeRequestDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<String> trainers;
    private List<TraineeTrainingResponseDTO> trainings;


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<String> trainers) {
        this.trainers = trainers;
    }

    public List<TraineeTrainingResponseDTO> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TraineeTrainingResponseDTO> trainings) {
        this.trainings = trainings;
    }
}