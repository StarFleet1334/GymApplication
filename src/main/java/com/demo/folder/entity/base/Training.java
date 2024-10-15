package com.demo.folder.entity.base;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "Trainee_Id", referencedColumnName = "id", nullable = true)
    private Trainee trainee;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "Trainer_Id", referencedColumnName = "id")
    private Trainer trainer;

    @Column(name = "Training_Name", nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "Training_Type_Id", referencedColumnName = "id")
    private TrainingType trainingType;

    @Column(name = "Training_Date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")

    private LocalDate trainingDate;

    @Column(name = "Training_Duration", nullable = false)
    private Number trainingDuration;

    public Training() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Number getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Number trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainee=" + trainee +
                ", trainer=" + trainer +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}