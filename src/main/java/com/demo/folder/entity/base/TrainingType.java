package com.demo.folder.entity.base;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "training_types")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Training_Type_Name", nullable = false)
    private String trainingTypeName;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings;

    @OneToMany(mappedBy = "specialization")
    private List<Trainer> trainers;

}