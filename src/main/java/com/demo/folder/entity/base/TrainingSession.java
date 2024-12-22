package com.demo.folder.entity.base;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
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

}
