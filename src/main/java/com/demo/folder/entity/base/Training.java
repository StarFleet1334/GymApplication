package com.demo.folder.entity.base;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
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

}