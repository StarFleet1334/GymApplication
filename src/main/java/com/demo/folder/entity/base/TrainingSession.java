package com.demo.folder.entity.base;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trainerUserName;

    @Column(nullable = false)
    private String trainerFirstName;

    @Column(nullable = false)
    private String trainerLastName;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private Integer trainingDuration;

    @Column(nullable = false)
    private String action;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTrainerUserName() {
        return trainerUserName;
    }
    public void setTrainerUserName(String trainerUserName) {
        this.trainerUserName = trainerUserName;
    }
    public String getTrainerFirstName() {
        return trainerFirstName;
    }
    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }
    public String getTrainerLastName() {
        return trainerLastName;
    }
    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public LocalDate getTrainingDate() {
        return trainingDate;
    }
    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }
    public Integer getTrainingDuration() {
        return trainingDuration;
    }
    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Boolean getActive() {
        return isActive;
    }
    public void setActive(Boolean active) {
        isActive = active;
    }
    @Override
    public String toString() {
        return "TrainingSession{" +
                "id=" + id +
                ", trainerUserName='" + trainerUserName + '\'' +
                ", trainerFirstName='" + trainerFirstName + '\'' +
                ", trainerLastName='" + trainerLastName + '\'' +
                ", isActive=" + isActive +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                ", action='" + action + '\'' +
                '}';
    }

}
