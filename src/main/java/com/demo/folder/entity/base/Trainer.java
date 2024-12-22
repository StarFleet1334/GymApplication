package com.demo.folder.entity.base;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "trainers")
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Specialization", referencedColumnName = "id")
    private TrainingType specialization;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "User_Id", referencedColumnName = "id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "trainer")
    private List<Training> trainings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees;

}