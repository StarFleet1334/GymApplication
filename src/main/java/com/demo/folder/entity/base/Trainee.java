package com.demo.folder.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@Table(name = "trainees")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Date_Of_Birth")
    private LocalDate dateOfBirth;
    @Column(name = "Address")
    private String address;

    @OneToMany(mappedBy = """
      trainee""", fetch = FetchType.EAGER)
    private List<Training> trainings;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "User_Id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers;

}